package com.fengxuechao.filter;

import brave.Span;
import brave.Tracer;
import org.slf4j.MDC;
import org.springframework.cloud.sleuth.instrument.web.TraceWebServletAutoConfiguration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * @author fengxuechao
 * @date 2022/5/24
 */
@Component
@Order(TraceWebServletAutoConfiguration.TRACING_FILTER_ORDER + 1)
public class CustomTraceMdcFilter extends GenericFilterBean {

    private final Tracer tracer;

    CustomTraceMdcFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        Span currentSpan = this.tracer.currentSpan();
        if (currentSpan == null) {
            chain.doFilter(request, response);
            return;
        }
        try {
            // MDC.put("test", "test");
            chain.doFilter(request, response);
        } finally {
            MDC.clear();//must be,threadLocal
        }
    }
}
