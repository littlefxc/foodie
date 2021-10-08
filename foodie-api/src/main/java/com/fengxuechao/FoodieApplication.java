package com.fengxuechao;

import com.fengxuechao.jvm.objectpool.datasource.MyDataSource;
import com.fengxuechao.jvm.objectpool.datasource.DataSourceEnpoint;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

import javax.sql.DataSource;

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
//@EnableRedisHttpSession // 开启使用redis作为spring session
@SpringBootApplication
@EnableDiscoveryClient
public class FoodieApplication {
    public static void main(String[] args) {
        System.err.println(System.getProperty("java.library.path"));
        SpringApplication.run(FoodieApplication.class, args);
    }

    @Bean
    ServletWebServerFactory servletWebServerFactory() {
        return new TomcatServletWebServerFactory();
    }

    @Bean
    @Primary
    public DataSource dataSource(){
        return new MyDataSource();
    }

    @Bean
    public DataSourceEnpoint dataSourceEnpoint() {
        DataSource dataSource = this.dataSource();
        return new DataSourceEnpoint((MyDataSource) dataSource);
    }
}
