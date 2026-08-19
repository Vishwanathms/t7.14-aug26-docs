package com.example.gatewayservice.filter;

import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

class RequestIdFilterTest {

    private final RequestIdFilter filter = new RequestIdFilter();

    @Test
    void generatesRequestIdWhenAbsent() {
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/students"));

        filter.filter(exchange, ex -> {
            String forwarded = ex.getRequest().getHeaders().getFirst(RequestIdFilter.HEADER);
            assertThat(forwarded).isNotBlank();
            return Mono.empty();
        }).block();

        assertThat(exchange.getResponse().getHeaders().getFirst(RequestIdFilter.HEADER)).isNotBlank();
    }

    @Test
    void preservesExistingRequestId() {
        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/students").header(RequestIdFilter.HEADER, "caller-supplied-id"));

        filter.filter(exchange, ex -> {
            String forwarded = ex.getRequest().getHeaders().getFirst(RequestIdFilter.HEADER);
            assertThat(forwarded).isEqualTo("caller-supplied-id");
            return Mono.empty();
        }).block();

        assertThat(exchange.getResponse().getHeaders().getFirst(RequestIdFilter.HEADER)).isEqualTo("caller-supplied-id");
    }
}
