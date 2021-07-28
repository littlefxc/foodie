/*
 * 版权所有: 爱WiFi无线运营中心
 * 创建日期: 2021/7/28
 * 创建作者: 冯雪超
 * 文件名称: Zip.java
 * 版本: v1.0
 * 功能:
 * 修改记录：
 */
package com.example.userappservice.config;

import io.opentracing.Tracer;
import io.opentracing.contrib.redis.common.TracingConfiguration;
import io.opentracing.contrib.redis.spring.data2.connection.TracingRedisConnectionFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.data.redis.JedisClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisPoolConfig;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;

/**
 * @author fengxuechao
 * @date 2021/7/28
 */
@Configuration
public class ZipkinRedisConfig {
    // ==================== Redis 相关 ====================

    @Bean
    public RedisConnectionFactory redisConnectionFactory(
            Tracer tracer,
            ObjectProvider<JedisClientConfigurationBuilderCustomizer> builderCustomizers,
            RedisProperties redisProperties) {
        // 创建 JedisConnectionFactory 对象
        RedisStandaloneConfiguration standaloneConfig = getStandaloneConfig(redisProperties);
        JedisClientConfiguration clientConfiguration = getJedisClientConfiguration(builderCustomizers, redisProperties);
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory(standaloneConfig, clientConfiguration);
        connectionFactory.afterPropertiesSet();
        // 创建 TracingConfiguration 对象
        TracingConfiguration tracingConfiguration = new TracingConfiguration.Builder(tracer)
                // 设置拓展 Tag ，设置 Redis 服务器地址。因为默认情况下，不会在操作 Redis 链路的 Span 上记录 Redis 服务器的地址，所以这里需要设置。
                .extensionTag("Server Address", redisProperties.getHost() + ":" + redisProperties.getPort())
                .build();
        // 创建 TracingRedisConnectionFactory 对象
        return new TracingRedisConnectionFactory(connectionFactory, tracingConfiguration);
    }

    private JedisClientConfiguration getJedisClientConfiguration(
            ObjectProvider<JedisClientConfigurationBuilderCustomizer> builderCustomizers,
            RedisProperties properties) {
        JedisClientConfiguration.JedisClientConfigurationBuilder builder = applyProperties(JedisClientConfiguration.builder(), properties);
        RedisProperties.Pool pool = properties.getJedis().getPool();
        if (pool != null) {
            applyPooling(pool, builder);
        }
        if (StringUtils.hasText(properties.getUrl())) {
            customizeConfigurationFromUrl(builder, properties);
        }
        builderCustomizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
        return builder.build();
    }

    private JedisClientConfiguration.JedisClientConfigurationBuilder applyProperties(
            JedisClientConfiguration.JedisClientConfigurationBuilder builder,
            RedisProperties properties) {
        if (properties.isSsl()) {
            builder.useSsl();
        }
        if (properties.getTimeout() != null) {
            Duration timeout = properties.getTimeout();
            builder.readTimeout(timeout).connectTimeout(timeout);
        }
        if (StringUtils.hasText(properties.getClientName())) {
            builder.clientName(properties.getClientName());
        }
        return builder;
    }

    private void applyPooling(RedisProperties.Pool pool,
                              JedisClientConfiguration.JedisClientConfigurationBuilder builder) {
        builder.usePooling().poolConfig(jedisPoolConfig(pool));
    }

    private JedisPoolConfig jedisPoolConfig(RedisProperties.Pool pool) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(pool.getMaxActive());
        config.setMaxIdle(pool.getMaxIdle());
        config.setMinIdle(pool.getMinIdle());
        if (pool.getTimeBetweenEvictionRuns() != null) {
            config.setTimeBetweenEvictionRunsMillis(pool.getTimeBetweenEvictionRuns().toMillis());
        }
        if (pool.getMaxWait() != null) {
            config.setMaxWaitMillis(pool.getMaxWait().toMillis());
        }
        return config;
    }

    protected final RedisStandaloneConfiguration getStandaloneConfig(RedisProperties properties) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        if (StringUtils.hasText(properties.getUrl())) {
            ConnectionInfo connectionInfo = parseUrl(properties.getUrl());
            config.setHostName(connectionInfo.getHostName());
            config.setPort(connectionInfo.getPort());
            config.setPassword(RedisPassword.of(connectionInfo.getPassword()));
        } else {
            config.setHostName(properties.getHost());
            config.setPort(properties.getPort());
            config.setPassword(RedisPassword.of(properties.getPassword()));
        }
        config.setDatabase(properties.getDatabase());
        return config;
    }

    protected ConnectionInfo parseUrl(String url) {
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            if (!"redis".equals(scheme) && !"rediss".equals(scheme)) {
                throw new RedisUrlSyntaxException(url);
            }
            boolean useSsl = ("rediss".equals(scheme));
            String password = null;
            if (uri.getUserInfo() != null) {
                password = uri.getUserInfo();
                int index = password.indexOf(':');
                if (index >= 0) {
                    password = password.substring(index + 1);
                }
            }
            return new ConnectionInfo(uri, useSsl, password);
        } catch (URISyntaxException ex) {
            throw new RedisUrlSyntaxException(url, ex);
        }
    }

    private void customizeConfigurationFromUrl(
            JedisClientConfiguration.JedisClientConfigurationBuilder builder,
            RedisProperties properties) {
        ConnectionInfo connectionInfo = parseUrl(properties.getUrl());
        if (connectionInfo.isUseSsl()) {
            builder.useSsl();
        }
    }

    static class ConnectionInfo {

        private final URI uri;

        private final boolean useSsl;

        private final String password;

        ConnectionInfo(URI uri, boolean useSsl, String password) {
            this.uri = uri;
            this.useSsl = useSsl;
            this.password = password;
        }

        boolean isUseSsl() {
            return this.useSsl;
        }

        String getHostName() {
            return this.uri.getHost();
        }

        int getPort() {
            return this.uri.getPort();
        }

        String getPassword() {
            return this.password;
        }

    }

    static class RedisUrlSyntaxException extends RuntimeException {

        private final String url;

        RedisUrlSyntaxException(String url, Exception cause) {
            super(buildMessage(url), cause);
            this.url = url;
        }

        RedisUrlSyntaxException(String url) {
            super(buildMessage(url));
            this.url = url;
        }

        private static String buildMessage(String url) {
            return "Invalid Redis URL '" + url + "'";
        }

        String getUrl() {
            return this.url;
        }

    }
}
