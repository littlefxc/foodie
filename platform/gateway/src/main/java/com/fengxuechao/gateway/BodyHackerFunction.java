package com.fengxuechao.gateway;

import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

/**
 * @author fengxuechao
 * @date 2022/5/6
 */
public interface BodyHackerFunction extends
        BiFunction<ServerHttpResponse, Publisher<? extends DataBuffer>, Mono<Void>> {
}
