package com.example.gatewayservice.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Stamps every request with an X-Request-Id (generating one if the caller
 * didn't send it) before it's forwarded downstream. With a single ingress
 * point in front of three services, this is the one place a correlation id
 * can be assigned once and carried through every hop - including
 * enrollment-service's outbound calls to student-service/course-service,
 * once those clients are updated to forward the header (see
 * Architecture-Recommendations.md, item 4).
 */
@Component
public class RequestIdFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RequestIdFilter.class);
    public static final String HEADER = "X-Request-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String requestId = request.getHeaders().getFirst(HEADER);
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }

        String finalRequestId = requestId;
        ServerHttpRequest mutatedRequest = request.mutate()
                .header(HEADER, finalRequestId)
                .build();

        log.info("{} {} {}={}", request.getMethod(), request.getURI().getPath(), HEADER, finalRequestId);

        exchange.getResponse().getHeaders().add(HEADER, finalRequestId);
        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
