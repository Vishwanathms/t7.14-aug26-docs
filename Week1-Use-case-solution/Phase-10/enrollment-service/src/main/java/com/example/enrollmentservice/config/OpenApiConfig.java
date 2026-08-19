package com.example.enrollmentservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI enrollmentServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Enrollment Service API")
                        .description("Owns enrollment records only - student and course details are "
                                + "resolved live over HTTP from student-service and course-service, not "
                                + "stored here. Part of the Student Management System microservices.")
                        .version("v1"));
    }
}
