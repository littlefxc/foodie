package com.fengxuechao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author fengxuechao
 * @date 2020/2/26
 * @see MapperScan 扫描 mybatis 通用 mapper 所在的包
 */
@MapperScan({"com.fengxuechao.mapper"})
// 扫描所有包以及相关组件包
@ComponentScan(basePackages = {"com.fengxuechao", "org.n3r.idworker"})
//@EnableTransactionManagement
@EnableScheduling       // 开启定时任务
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
