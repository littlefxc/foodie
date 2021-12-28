package com.fengxuechao.ratelimiter.annotation;

import java.lang.annotation.*;

/**
 * @author fengxuechao
 * @date 2021/12/17
 */
@Target(ElementType.METHOD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessLimiter {

    /**
     * 限流 KEY
     *
     * @return ""
     */
    String key() default "";

    /**
     * 当前流量大小
     *
     * @return number
     */
    int limit() default 1;
}
