package com.fengxuechao.ratelimiter;

import com.fengxuechao.ratelimiter.annotation.AccessLimiter;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author fengxuechao
 * @date 2021/12/14
 */
@Slf4j
@RestController
public class RateLimiterController {

    private RateLimiter rateLimiter = RateLimiter.create(2.0);

    /**
     * 非阻塞限流
     *
     * @param count   请求令牌数
     * @param timeout 请求令牌超时时间, 如果等于0, 表示不限定时间
     * @return
     */
    @GetMapping("/tryAcquire")
    public String tryAcquire(Integer count, @RequestParam(defaultValue = "0") Integer timeout) {
        if (rateLimiter.tryAcquire(count, timeout, TimeUnit.SECONDS)) {
            log.info("success, rate is {}", rateLimiter.getRate());
            return "success";
        } else {
            log.info("fail, rate is {}", rateLimiter.getRate());
            return "fail";
        }
    }

    /**
     * 同步阻塞式限流
     *
     * @param count 请求令牌数
     * @return
     */
    @GetMapping("/acquire")
    public String acquire(@RequestParam(defaultValue = "1") Integer count) {
        rateLimiter.acquire(count);
        log.info("success, rate is {}", rateLimiter.getRate());
        return "success";
    }

    @GetMapping("/annotation")
    @AccessLimiter
    public String acquireAnnotation() {
        return "success";
    }

    /**
     * Nginx 专用
     * 1. 修改 hosts 文件，内容：127.0.0.1 www.rate-limiter.com
     * 2. 修改 nginx 的路由规则
     * @return
     */
    @GetMapping("/nginx")
    public String nginx(@RequestParam(defaultValue = "0") Integer sleep) {
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
        }
        log.info("nginx success!");
        return "nginx success!";
    }
}
