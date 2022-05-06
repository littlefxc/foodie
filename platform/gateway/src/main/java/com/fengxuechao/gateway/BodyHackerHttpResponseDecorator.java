package com.fengxuechao.gateway;

import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import reactor.core.publisher.Mono;

public class BodyHackerHttpResponseDecorator extends ServerHttpResponseDecorator {

    /**
     * 负责具体写入Body内容的代理类
     */
    private BodyHackerFunction delegate = null;

    public BodyHackerHttpResponseDecorator(BodyHackerFunction bodyHandler, ServerHttpResponse delegate) {
        super(delegate);
        this.delegate = bodyHandler;
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        return delegate.apply(getDelegate(), body);
    }

}