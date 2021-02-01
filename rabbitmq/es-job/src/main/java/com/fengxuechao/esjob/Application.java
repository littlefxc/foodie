package com.fengxuechao.esjob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.fengxuechao.rabbit.task.annotation.EnableElasticJob;

@EnableElasticJob
@SpringBootApplication
@ComponentScan(basePackages = {"com.fengxuechao.esjob.*","com.fengxuechao.esjob.service.*", "com.fengxuechao.esjob.annotation.*","com.fengxuechao.esjob.task.*"})
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
