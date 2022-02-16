package com.fengxuechao.ratelimiter;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

/**
 * @author fengxuechao
 * @date 2021/12/16
 */
@Slf4j
@Component
public class RedisLuaAccessLimiter {

    private StringRedisTemplate redisTemplate;

    private RedisScript<Boolean> redisScript;

    public RedisLuaAccessLimiter(
            StringRedisTemplate stringRedisTemplate,
            @Value("classpath:/ratelimiter.lua") Resource resource) {
        this.redisTemplate = stringRedisTemplate;
        redisScript = RedisScript.of(resource, Boolean.class);
    }

    public void limitAccess(String key, Integer limit) {
        Boolean acquired = false;
        acquired = redisTemplate.execute(redisScript, Lists.newArrayList(key), limit.toString());
        if (!acquired) {
            log.error("your access is blocked, key is {}", key);
            throw new RuntimeException("Your access is blocked!");
        }
    }
}
