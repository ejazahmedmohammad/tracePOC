package org.example;

import brave.Span;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import brave.Tracer;

@Component
public class TraceIdResponseHeaderFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(TraceIdResponseHeaderFilter.class);

    private final Tracer tracer;

    public TraceIdResponseHeaderFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        Span newSpan = tracer.nextSpan().name("gateway-span").start();
        try (Tracer.SpanInScope ws = tracer.withSpanInScope(newSpan)) {
            String tid = newSpan.context().traceIdString();
            logger.info("traceId {} ", tid );
            exchange = exchange.mutate()
                    .request(r -> r.headers(headers -> {
                        headers.add("X-B3-TraceId",tid);
                        headers.add("X-B3-SpanId",newSpan.context().spanIdString());
                    }))
                    .build();
            ServerWebExchange finalExchange = exchange;
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                ServerHttpResponse response = finalExchange.getResponse();
                    response.getHeaders().add("X-TraceId", tid);

            }));
        } finally {
            // Finish the span
            newSpan.finish();
        }
    }

    @Override
    public int getOrder() {
        // Set the order if you have other filters you want to execute before or after
        return Ordered.LOWEST_PRECEDENCE;
    }
}