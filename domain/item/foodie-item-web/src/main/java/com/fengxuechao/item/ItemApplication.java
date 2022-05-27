package com.fengxuechao.item;

import org.slf4j.MDC;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author fengxuechao
 * @date 2021/10/20
 */
// 扫描 mybatis 通用 mapper 所在的包
@MapperScan(basePackages = "com.fengxuechao.item.mapper")
// 扫描所有包以及相关组件包
@ComponentScan(basePackages = {"com.fengxuechao", "org.n3r.idworker"})
@EnableDiscoveryClient
@SpringBootApplication
public class ItemApplication {
    public static void main(String[] args) {
        SpringApplication.run(ItemApplication.class, args);
    }
}
