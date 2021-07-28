package com.example.userappservice.service.impl;

import com.example.userappservice.entity.User;
import com.example.userappservice.mapper.UserMapper;
import com.example.userappservice.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.opentracing.Span;
import io.opentracing.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author fengxuechao
 * @date 2021/7/28
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private static final String REDIS_USER_PREFIX = "user:";

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private Tracer tracer;

    @Autowired
    private AsyncService asyncService;

    @Override
    public User getUserById(Integer id) throws JsonProcessingException {

        // 创建一个 Span
        Span span = tracer.buildSpan("getUserById(" + id + ")")
                .withTag("service.class", getClass().getSimpleName())
                .start();

        String key = REDIS_USER_PREFIX + id;
        String content = redisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(content)) {
            return objectMapper.readValue(content, User.class);
        }
        User user = userMapper.getById(id);
        log.info("主线程 = {}, Name = {}", Thread.currentThread().getId(), Thread.currentThread().getName());
        asyncService.setRedis(key, user);
        span.finish();
        return user;
    }


}
