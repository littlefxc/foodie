package com.fengxuechao.demo.dubbbo;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author fengxuechao
 * @date 2022/6/8
 */
@EnableDubbo
@SpringBootApplication
public class DubboConsumerApp {

    public static void main(String[] args) {
        SpringApplication.run(DubboConsumerApp.class, args);
    }
}
