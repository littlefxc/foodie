package com.fengxuechao.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

//@Controller
@RefreshScope
@ApiIgnore
@RestController
public class HelloController {

    final static Logger logger = LoggerFactory.getLogger(HelloController.class);

    @Value("${useLocalCache:false}")
    private boolean useLocalCache;

    @GetMapping("/nacos/get")
    public boolean getNacosConfig() {
        return useLocalCache;
    }

    @GetMapping("/hello")
    public Object hello() {

        logger.debug("debug: hello~");
        logger.info("info: hello~");
        logger.warn("warn: hello~");
        logger.error("error: hello~");

        return "Hello World~";
    }

    @GetMapping("/async/future")
    public CompletableFuture<String> getCompletableFutureString() throws InterruptedException {
        Thread.sleep(2000L);
        logger.info("hello, completetablefuture!");
        return CompletableFuture.supplyAsync(() -> {
            logger.info("hello, completetablefuture!");
            return "hello, completetablefuture!";
        });
    }

    @GetMapping("/async/callable")
    public Callable<String> getCallableString() throws InterruptedException {
        Thread.sleep(2000L);
        logger.info("hello, Callable !");
        return () -> {
            logger.info("hello, Callable !");
            return "Hello, Callable !";
        };
    }

    @GetMapping("/setSession")
    public Object setSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute("userInfo", "new user");
        session.setMaxInactiveInterval(3600);
        session.getAttribute("userInfo");
//        session.removeAttribute("userInfo");
        return "ok";
    }

}
