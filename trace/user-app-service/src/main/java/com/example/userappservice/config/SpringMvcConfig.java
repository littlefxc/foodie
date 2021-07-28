/*
 * 版权所有: 爱WiFi无线运营中心
 * 创建日期: 2021/7/28
 * 创建作者: 冯雪超
 * 文件名称: SpringMVCConfig.java
 * 版本: v1.0
 * 功能:
 * 修改记录：
 */
package com.example.userappservice.config;

import brave.spring.webmvc.SpanCustomizingAsyncHandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author fengxuechao
 * @date 2021/7/28
 */
@Configuration
@Import(SpanCustomizingAsyncHandlerInterceptor.class)
public class SpringMvcConfig implements WebMvcConfigurer {

    @Autowired
    private SpanCustomizingAsyncHandlerInterceptor webMvcTracingCustomizer;

    /**
     * Decorates server spans with application-defined web tags
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) { // 记录 SpringMVC 相关信息到 Span 中
        registry.addInterceptor(webMvcTracingCustomizer);
    }
}
