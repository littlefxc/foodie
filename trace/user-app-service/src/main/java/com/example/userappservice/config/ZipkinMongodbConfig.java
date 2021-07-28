/*
 * 版权所有: 爱WiFi无线运营中心
 * 创建日期: 2021/7/28
 * 创建作者: 冯雪超
 * 文件名称: ZipkinMongodnConfig.java
 * 版本: v1.0
 * 功能:
 * 修改记录：
 */
package com.example.userappservice.config;

import com.mongodb.MongoClientSettings;
import io.opentracing.Tracer;
import io.opentracing.contrib.mongo.common.TracingCommandListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author fengxuechao
 * @date 2021/7/28
 */
@Configuration
public class ZipkinMongodbConfig {
    // ==================== MongoDB 相关 ====================

    @Bean
    @ConditionalOnProperty(prefix = "spring.data.mongodb", name = "host")
    public MongoClientSettings mongoClientOptions(Tracer tracer) {
        // 创建 TracingCommandListener 对象
        TracingCommandListener listener = new TracingCommandListener.Builder(tracer).build();
        // 创建 MongoClientOptions 对象，并设置监听器
        return MongoClientSettings.builder().addCommandListener(listener).build();
    }
}
