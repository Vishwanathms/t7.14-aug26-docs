package com.example.gatewayservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Confirms the gateway's Spring context - including all 6 routes defined in
 * application.yml - starts cleanly. Route-by-route behavior (does /api/students
 * actually reach student-service) is verified against the running Docker
 * Compose stack instead of here, since that's a question about the network
 * topology between containers, not something a unit-level context load can
 * answer meaningfully.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GatewayServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}
