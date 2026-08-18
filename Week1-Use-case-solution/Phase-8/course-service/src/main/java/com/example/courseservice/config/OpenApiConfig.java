package com.example.courseservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI courseServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Course Service API")
                        .description("Owns the course catalog, including each course's enrollment capacity. "
                                + "Part of the Student Management System microservices - see also "
                                + "student-service and enrollment-service.")
                        .version("v1"));
    }
}
