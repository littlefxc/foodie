package com.fengxuechao.order;

import com.fengxuechao.item.service.ItemService;
import com.fengxuechao.order.fallback.itemservice.ItemCommentsFallbackFactory;
import com.fengxuechao.order.fallback.itemservice.ItemCommentsFeignClient;
import com.fengxuechao.user.service.AddressService;
import com.fengxuechao.user.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author fengxuechao
 * @date 2021/10/21
 */
@SpringBootApplication
// 扫描 mybatis 通用 mapper 所在的包
@MapperScan(basePackages = "com.fengxuechao.order.mapper")
// 扫描所有包以及相关组件包
@ComponentScan(basePackages = {"com.fengxuechao", "org.n3r.idworker"})
@EnableDiscoveryClient
@EnableScheduling
@EnableFeignClients(
        clients = {
                ItemCommentsFeignClient.class,
                ItemService.class,
                UserService.class,
                AddressService.class
        }
//        basePackages = {
//            "com.fengxuechao.user.service",
//            "com.fengxuechao.item.service",
//            "com.fengxuechao.order.fallback.itemservice"
//        }
)
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
