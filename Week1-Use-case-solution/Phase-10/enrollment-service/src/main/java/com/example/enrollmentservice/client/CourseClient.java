package com.example.enrollmentservice.client;

import com.example.enrollmentservice.dto.CourseDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Optional;

@Component
public class CourseClient {

    private final RestClient restClient;

    public CourseClient(@Qualifier("courseServiceClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public Optional<CourseDto> findById(Long id) {
        try {
            CourseDto course = restClient.get()
                    .uri("/api/courses/{id}", id)
                    .retrieve()
                    .body(CourseDto.class);
            return Optional.ofNullable(course);
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().value() == 404) {
                return Optional.empty();
            }
            throw ex;
        }
    }
}
