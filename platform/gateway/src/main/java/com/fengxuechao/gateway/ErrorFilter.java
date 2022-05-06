package com.fengxuechao.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class ErrorFilter implements GatewayFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        final ServerHttpRequest request = exchange.getRequest();
        BodyHackerFunction delegate = (resp, body) -> Flux.from(body)
                .flatMap(orgBody -> {
                    // 原始的response body
                    byte[] orgContent = new byte[orgBody.readableByteCount()];
                    orgBody.read(orgContent);

                    String content = new String(orgContent);
                    log.info("original content {}", content);

                    // 如果500错误，则替换
                    if (resp.getStatusCode().value() == 500) {
                        content = String.format("{\"status\": %d,\"path\":\"%s\"}",
                                resp.getStatusCode().value(),
                                request.getPath().value());
                    }

                    // 告知客户端Body的长度，如果不设置的话客户端会一直处于等待状态不结束
                    HttpHeaders headers = resp.getHeaders();
                    headers.setContentLength(content.length());
                    return resp.writeWith(Flux.just(content)
                            .map(bx -> resp.bufferFactory().wrap(bx.getBytes())));
                }).then();

        // 将装饰器当做Response返回
        BodyHackerHttpResponseDecorator responseDecorator =
                new BodyHackerHttpResponseDecorator(delegate, exchange.getResponse());
            
        return chain.filter(exchange.mutate().response(responseDecorator).build());
    }

    @Override
    public int getOrder() {
        // WRITE_RESPONSE_FILTER的执行顺序是-1，我们的Hacker在它之前执行
        return -2;
    }

}
