package com.example.userappservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class UserAppServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserAppServiceApplication.class, args);
    }

}
