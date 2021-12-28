package com.fengxuechao.ratelimiter.annotation;

import com.fengxuechao.ratelimiter.RedisLuaAccessLimiter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author fengxuechao
 * @date 2021/12/17
 */
@Slf4j
@Aspect
@Component
public class AccessLimiterAspect {

    @Autowired
    private RedisLuaAccessLimiter redisLuaAccessLimiter;

    @Pointcut("@annotation(AccessLimiter)")
    public void cut() {

    }

    @Before(value = "cut()")
    public void before(JoinPoint joinPoint) {
        // 获取方法签名作为 KEY
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        AccessLimiter annotation = method.getAnnotation(AccessLimiter.class);
        String key = annotation.key();
        if (StringUtils.isBlank(key)) {
            // 如果没有设置 key，就自动生成
            Class<?>[] parameterTypes = method.getParameterTypes();
            key = method.getName();
            String paramTypes = Arrays.stream(parameterTypes)
                    .map(Class::getName)
                    .collect(Collectors.joining(","));
            key += ("#" + paramTypes);
        }
        Integer limit = annotation.limit();

        // 调用redis
        redisLuaAccessLimiter.limitAccess(key, limit);
    }
}
