package com.example.enrollmentservice.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(ServiceUrlsConfig.class)
public class RestClientConfig {

    @Bean
    public RestClient studentServiceClient(ServiceUrlsConfig urls) {
        return RestClient.create(urls.getStudentUrl());
    }

    @Bean
    public RestClient courseServiceClient(ServiceUrlsConfig urls) {
        return RestClient.create(urls.getCourseUrl());
    }
}
