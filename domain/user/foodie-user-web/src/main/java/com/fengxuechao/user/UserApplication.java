package com.fengxuechao.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author fengxuechao
 * @date 2021/10/20
 */
@SpringBootApplication
// 扫描 mybatis 通用 mapper 所在的包
@MapperScan(basePackages = "com.fengxuechao.user.mapper")
// 扫描所有包以及相关组件包
@ComponentScan(basePackages = {"com.fengxuechao", "org.n3r.idworker"})
@EnableDiscoveryClient
@EnableCircuitBreaker
@EnableFeignClients(basePackages = {"com.fengxuechao.auth"})
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }

}
