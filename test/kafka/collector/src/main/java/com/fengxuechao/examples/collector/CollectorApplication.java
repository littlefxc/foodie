package com.fengxuechao.examples.collector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CollectorApplication {


	public static void main(String[] args) {
		
        SpringApplication app = new SpringApplication(CollectorApplication.class);
        app.run(args);
	
	}
	
}

