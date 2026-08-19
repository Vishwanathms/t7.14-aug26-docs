package com.example.studentservice.integration;

import com.example.studentservice.dto.StudentRequest;
import com.example.studentservice.dto.StudentStatusRequest;
import com.example.studentservice.model.StudentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    void createThenFetchStudentRoundTripsThroughRealDatabase() {
        StudentRequest request = new StudentRequest("Integration Test", "integration@example.com");

        ResponseEntity<String> createResponse = restTemplate.postForEntity(url("/api/students"), request, String.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).contains("Integration Test").contains("\"status\":\"ACTIVE\"");

        ResponseEntity<String> listResponse = restTemplate.getForEntity(url("/api/students"), String.class);
        assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listResponse.getBody()).contains("integration@example.com");
    }

    @Test
    void duplicateEmailReturns409() {
        StudentRequest request = new StudentRequest("Dup", "dup-integration@example.com");
        restTemplate.postForEntity(url("/api/students"), request, String.class);

        ResponseEntity<String> secondResponse = restTemplate.postForEntity(url("/api/students"), request, String.class);

        assertThat(secondResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void statusTransitionRoundTripsThenRejectsLeavingGraduated() {
        StudentRequest request = new StudentRequest("Status Test", "status-test@example.com");
        ResponseEntity<String> createResponse = restTemplate.postForEntity(url("/api/students"), request, String.class);
        String body = createResponse.getBody();
        Long id = Long.valueOf(body.replaceAll(".*\"id\":(\\d+).*", "$1"));

        ResponseEntity<String> graduate = restTemplate.exchange(
                url("/api/students/" + id + "/status"), HttpMethod.PATCH,
                new org.springframework.http.HttpEntity<>(new StudentStatusRequest(StudentStatus.GRADUATED)), String.class);
        assertThat(graduate.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(graduate.getBody()).contains("\"status\":\"GRADUATED\"");

        ResponseEntity<String> reactivate = restTemplate.exchange(
                url("/api/students/" + id + "/status"), HttpMethod.PATCH,
                new org.springframework.http.HttpEntity<>(new StudentStatusRequest(StudentStatus.ACTIVE)), String.class);
        assertThat(reactivate.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void missingStudentReturns404() {
        ResponseEntity<String> response = restTemplate.getForEntity(url("/api/students/999999"), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
