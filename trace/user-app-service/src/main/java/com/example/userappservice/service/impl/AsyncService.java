/*
 * 版权所有: 爱WiFi无线运营中心
 * 创建日期: 2021/7/28
 * 创建作者: 冯雪超
 * 文件名称: AsyncService.java
 * 版本: v1.0
 * 功能:
 * 修改记录：
 */
package com.example.userappservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author fengxuechao
 * @date 2021/7/28
 */
@Slf4j
@Component
public class AsyncService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Async
    public void setRedis(String key, Object value) throws JsonProcessingException {
        log.info("异步线程 ID = {}, Name = {}", Thread.currentThread().getId(), Thread.currentThread().getName());
        String json = objectMapper.writeValueAsString(value);
        redisTemplate.opsForValue().set(key, json, 5, TimeUnit.SECONDS);
    }
}
