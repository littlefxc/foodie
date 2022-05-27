package com.fengxuechao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import zipkin2.server.internal.EnableZipkinServer;

/**
 * @author fengxuechao
 * @date 2022/5/17
 */
@EnableZipkinServer
@SpringBootApplication
public class ZipkinServerApplication {

    public static void main(String[] args) {
        System.setProperty("KAFKA_BOOTSTRAP_SERVERS", "localhost:9200");
        SpringApplication.run(ZipkinServerApplication.class, args);
    }
}
