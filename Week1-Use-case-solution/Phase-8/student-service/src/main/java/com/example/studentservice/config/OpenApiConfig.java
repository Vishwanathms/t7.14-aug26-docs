package com.example.studentservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI studentServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Student Service API")
                        .description("Owns student records: registration, lookup, search, and status "
                                + "(ACTIVE / INACTIVE / GRADUATED). Part of the Student Management System "
                                + "microservices - see also course-service and enrollment-service.")
                        .version("v1"));
    }
}
