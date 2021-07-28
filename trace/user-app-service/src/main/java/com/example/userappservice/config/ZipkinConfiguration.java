package com.example.userappservice.config;

import brave.CurrentSpanCustomizer;
import brave.SpanCustomizer;
import brave.Tracing;
import brave.http.HttpTracing;
import brave.opentracing.BraveTracer;
import brave.servlet.TracingFilter;
import brave.spring.webmvc.SpanCustomizingAsyncHandlerInterceptor;
import io.opentracing.Tracer;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Sender;
import zipkin2.reporter.brave.AsyncZipkinSpanHandler;
import zipkin2.reporter.okhttp3.OkHttpSender;

import javax.servlet.Filter;

/**
 * Zipkin 链路追踪相关的 Bean
 *
 * @author fengxuechao
 * @date 2021/7/27
 */
@EnableConfigurationProperties(RedisProperties.class)
@Configuration
@Import(SpanCustomizingAsyncHandlerInterceptor.class)
public class ZipkinConfiguration implements WebMvcConfigurer {

    // ==================== 通用配置 ====================

    @Bean
    public Tracer openTracer(Tracing tracing) {
        return BraveTracer.create(tracing);
    }

    /**
     * Configuration for how to send spans to Zipkin
     */
    @Bean
    public Sender sender() { // Sender 采用 HTTP 通信方式
        return OkHttpSender.create("http://127.0.0.1:9411/api/v2/spans");
    }

    /**
     * Configuration for how to buffer spans into messages for Zipkin
     */
    @Bean
    public AsyncReporter<Span> spanReporter() { // 异步 Reporter
        return AsyncReporter.create(sender());
    }

    /**
     * Controls aspects of tracing such as the service name that shows up in the UI
     */
    @Bean
    public Tracing tracing() {
        return Tracing.newBuilder()
                // 应用名
                .localServiceName("user-app-service")
                .addSpanHandler(AsyncZipkinSpanHandler.create(sender()))
                .build();
    }

    /**
     * Allows someone to add tags to a span if a trace is in progress
     */
    @Bean
    public SpanCustomizer spanCustomizer(Tracing tracing) {
        return CurrentSpanCustomizer.create(tracing);
    }

    // ==================== HTTP 相关 ====================

    /**
     * Decides how to name and tag spans. By default they are named the same as the http method
     */
    @Bean
    public HttpTracing httpTracing(Tracing tracing) {
        return HttpTracing.create(tracing);
    }

    /**
     * Creates server spans for http requests
     */
    @Bean
    public Filter tracingFilter(HttpTracing httpTracing) { // 拦截请求，记录 HTTP 请求的链路信息
        return TracingFilter.create(httpTracing);
    }

    // ==================== SpringMVC 相关 ====================
    // @see SpringMvcConfiguration 类上的，@Import(SpanCustomizingAsyncHandlerInterceptor.class) 。
    // 因为 SpanCustomizingAsyncHandlerInterceptor 未提供 public 构造方法


}
