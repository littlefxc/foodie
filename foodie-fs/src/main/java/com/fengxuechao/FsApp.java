package com.fengxuechao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication//(exclude = {SecurityAutoConfiguration.class})
@MapperScan(basePackages = "com.fengxuechao.mapper")
@ComponentScan(basePackages = {"com.fengxuechao", "org.n3r.idworker"})
public class FsApp {

    public static void main(String[] args) {
        SpringApplication.run(FsApp.class, args);
    }

}
