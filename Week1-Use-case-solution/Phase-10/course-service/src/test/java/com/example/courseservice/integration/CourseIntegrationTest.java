package com.example.courseservice.integration;

import com.example.courseservice.dto.CourseRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CourseIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    void createThenFetchCourseRoundTripsThroughRealDatabase() {
        CourseRequest request = new CourseRequest("Integration Course", 25);

        ResponseEntity<String> createResponse = restTemplate.postForEntity(url("/api/courses"), request, String.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).contains("Integration Course").contains("\"capacity\":25");

        ResponseEntity<String> listResponse = restTemplate.getForEntity(url("/api/courses"), String.class);
        assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listResponse.getBody()).contains("Integration Course");
    }

    @Test
    void invalidCourseReturns400() {
        CourseRequest request = new CourseRequest("", null);

        ResponseEntity<String> response = restTemplate.postForEntity(url("/api/courses"), request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void missingCourseReturns404() {
        ResponseEntity<String> response = restTemplate.getForEntity(url("/api/courses/999999"), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateThenDeleteCourseRoundTripsThroughRealDatabase() {
        CourseRequest createRequest = new CourseRequest("Original Title", 20);
        ResponseEntity<String> createResponse = restTemplate.postForEntity(url("/api/courses"), createRequest, String.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Integer id = com.jayway.jsonpath.JsonPath.read(createResponse.getBody(), "$.id");

        CourseRequest updateRequest = new CourseRequest("Updated Title", 35);
        ResponseEntity<String> updateResponse = restTemplate.exchange(
                url("/api/courses/" + id), org.springframework.http.HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(updateRequest), String.class);
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody()).contains("Updated Title").contains("\"capacity\":35");

        restTemplate.delete(url("/api/courses/" + id));

        ResponseEntity<String> afterDelete = restTemplate.getForEntity(url("/api/courses/" + id), String.class);
        assertThat(afterDelete.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updatingMissingCourseReturns404() {
        CourseRequest request = new CourseRequest("X", 1);

        ResponseEntity<String> response = restTemplate.exchange(
                url("/api/courses/999999"), org.springframework.http.HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(request), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
