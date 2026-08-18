package com.example.enrollmentservice.service;

import com.example.enrollmentservice.client.CourseClient;
import com.example.enrollmentservice.client.StudentClient;
import com.example.enrollmentservice.dto.CourseDto;
import com.example.enrollmentservice.dto.EnrollmentResponse;
import com.example.enrollmentservice.dto.StudentDto;
import com.example.enrollmentservice.exception.AlreadyEnrolledException;
import com.example.enrollmentservice.exception.CourseCapacityExceededException;
import com.example.enrollmentservice.exception.CourseNotFoundException;
import com.example.enrollmentservice.exception.StudentNotFoundException;
import com.example.enrollmentservice.model.Enrollment;
import com.example.enrollmentservice.repository.EnrollmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private StudentClient studentClient;

    @Mock
    private CourseClient courseClient;

    @InjectMocks
    private EnrollmentService enrollmentService;

    private StudentDto student;
    private CourseDto course;

    @BeforeEach
    void setUp() {
        student = new StudentDto();
        student.setId(1L);
        student.setName("Rahul");

        course = new CourseDto();
        course.setId(1L);
        course.setTitle("Java");
        course.setCapacity(30);
    }

    @Test
    void enrollSucceedsWhenStudentAndCourseExistAndRoomAvailable() {
        given(studentClient.findById(1L)).willReturn(Optional.of(student));
        given(courseClient.findById(1L)).willReturn(Optional.of(course));
        given(enrollmentRepository.existsByStudentIdAndCourseId(1L, 1L)).willReturn(false);
        given(enrollmentRepository.countByCourseId(1L)).willReturn(0L);
        given(enrollmentRepository.save(any(Enrollment.class))).willAnswer(inv -> {
            Enrollment e = inv.getArgument(0);
            e.setId(10L);
            return e;
        });

        EnrollmentResponse response = enrollmentService.enroll(1L, 1L);

        assertThat(response.getStudentName()).isEqualTo("Rahul");
        assertThat(response.getCourseTitle()).isEqualTo("Java");
    }

    @Test
    void enrollThrowsWhenStudentNotFound() {
        given(studentClient.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> enrollmentService.enroll(1L, 1L))
                .isInstanceOf(StudentNotFoundException.class);

        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void enrollThrowsWhenCourseNotFound() {
        given(studentClient.findById(1L)).willReturn(Optional.of(student));
        given(courseClient.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> enrollmentService.enroll(1L, 1L))
                .isInstanceOf(CourseNotFoundException.class);

        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void enrollThrowsWhenAlreadyEnrolled() {
        given(studentClient.findById(1L)).willReturn(Optional.of(student));
        given(courseClient.findById(1L)).willReturn(Optional.of(course));
        given(enrollmentRepository.existsByStudentIdAndCourseId(1L, 1L)).willReturn(true);

        assertThatThrownBy(() -> enrollmentService.enroll(1L, 1L))
                .isInstanceOf(AlreadyEnrolledException.class);

        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void enrollThrowsWhenCourseAtCapacity() {
        given(studentClient.findById(1L)).willReturn(Optional.of(student));
        given(courseClient.findById(1L)).willReturn(Optional.of(course));
        given(enrollmentRepository.existsByStudentIdAndCourseId(1L, 1L)).willReturn(false);
        given(enrollmentRepository.countByCourseId(1L)).willReturn(30L);

        assertThatThrownBy(() -> enrollmentService.enroll(1L, 1L))
                .isInstanceOf(CourseCapacityExceededException.class);

        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void enrollSucceedsAtOneSeatBelowCapacity() {
        given(studentClient.findById(1L)).willReturn(Optional.of(student));
        given(courseClient.findById(1L)).willReturn(Optional.of(course));
        given(enrollmentRepository.existsByStudentIdAndCourseId(1L, 1L)).willReturn(false);
        given(enrollmentRepository.countByCourseId(1L)).willReturn(29L);
        given(enrollmentRepository.save(any(Enrollment.class))).willAnswer(inv -> inv.getArgument(0));

        EnrollmentResponse response = enrollmentService.enroll(1L, 1L);

        assertThat(response).isNotNull();
    }
}
