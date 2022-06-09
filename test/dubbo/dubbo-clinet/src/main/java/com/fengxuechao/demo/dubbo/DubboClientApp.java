package com.fengxuechao.demo.dubbo;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author fengxuechao
 * @date 2022/6/7
 */
@EnableDubbo
@SpringBootApplication
public class DubboClientApp {

    public static void main(String[] args) {
        SpringApplication.run(DubboClientApp.class, args);
    }
}
