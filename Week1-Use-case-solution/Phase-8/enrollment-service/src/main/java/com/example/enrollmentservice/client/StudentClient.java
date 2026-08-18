package com.example.enrollmentservice.client;

import com.example.enrollmentservice.dto.StudentDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Optional;

@Component
public class StudentClient {

    private final RestClient restClient;

    public StudentClient(@Qualifier("studentServiceClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public Optional<StudentDto> findById(Long id) {
        try {
            StudentDto student = restClient.get()
                    .uri("/api/students/{id}", id)
                    .retrieve()
                    .body(StudentDto.class);
            return Optional.ofNullable(student);
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().value() == 404) {
                return Optional.empty();
            }
            throw ex;
        }
    }
}
