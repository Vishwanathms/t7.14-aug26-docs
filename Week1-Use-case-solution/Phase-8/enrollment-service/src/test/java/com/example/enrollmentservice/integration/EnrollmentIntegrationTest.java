package com.example.enrollmentservice.integration;

import com.example.enrollmentservice.client.CourseClient;
import com.example.enrollmentservice.client.StudentClient;
import com.example.enrollmentservice.dto.CourseDto;
import com.example.enrollmentservice.dto.StudentDto;
import com.example.enrollmentservice.repository.EnrollmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

/**
 * Full Spring context + a real H2 database for the enrollment table itself,
 * but StudentClient/CourseClient are mocked - enrollment-service's only
 * external dependencies are two other network services that aren't running
 * during this test, so this is the honest integration boundary: everything
 * enrollment-service actually owns is exercised for real, the two HTTP
 * calls it makes outward are stubbed.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EnrollmentIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @MockBean
    private StudentClient studentClient;

    @MockBean
    private CourseClient courseClient;

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @BeforeEach
    void setUp() {
        enrollmentRepository.deleteAll();

        StudentDto student = new StudentDto();
        student.setId(1L);
        student.setName("Rahul");
        given(studentClient.findById(1L)).willReturn(Optional.of(student));
        given(studentClient.findById(eq(999999L))).willReturn(Optional.empty());

        CourseDto smallCourse = new CourseDto();
        smallCourse.setId(1L);
        smallCourse.setTitle("Java");
        smallCourse.setCapacity(1);
        given(courseClient.findById(1L)).willReturn(Optional.of(smallCourse));
    }

    @Test
    void enrollThenLookupRoundTripsThroughRealDatabase() {
        ResponseEntity<String> enrollResponse = restTemplate.postForEntity(
                url("/api/enrollments?studentId=1&courseId=1"), null, String.class);
        assertThat(enrollResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(enrollResponse.getBody()).contains("Rahul").contains("Java");

        ResponseEntity<String> lookupResponse = restTemplate.getForEntity(
                url("/api/enrollments/student/1"), String.class);
        assertThat(lookupResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(lookupResponse.getBody()).contains("Java");
    }

    @Test
    void duplicateEnrollmentReturns409() {
        restTemplate.postForEntity(url("/api/enrollments?studentId=1&courseId=1"), null, String.class);

        ResponseEntity<String> second = restTemplate.postForEntity(
                url("/api/enrollments?studentId=1&courseId=1"), null, String.class);

        assertThat(second.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void courseAtCapacityRejectsSecondStudent() {
        StudentDto secondStudent = new StudentDto();
        secondStudent.setId(2L);
        secondStudent.setName("Priya");
        given(studentClient.findById(2L)).willReturn(Optional.of(secondStudent));

        ResponseEntity<String> first = restTemplate.postForEntity(
                url("/api/enrollments?studentId=1&courseId=1"), null, String.class);
        assertThat(first.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // course capacity is 1 (see setUp), so a second, different student must be rejected
        ResponseEntity<String> second = restTemplate.postForEntity(
                url("/api/enrollments?studentId=2&courseId=1"), null, String.class);
        assertThat(second.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(second.getBody()).contains("capacity");
    }

    @Test
    void enrollingUnknownStudentReturns404() {
        ResponseEntity<String> response = restTemplate.postForEntity(
                url("/api/enrollments?studentId=999999&courseId=1"), null, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
