package com.fengxuechao;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author fengxuechao
 * @date 2020/2/26
 * @see MapperScan 扫描 mybatis 通用 mapper 所在的包
 */
@MapperScan({"com.fengxuechao.mapper"})
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
