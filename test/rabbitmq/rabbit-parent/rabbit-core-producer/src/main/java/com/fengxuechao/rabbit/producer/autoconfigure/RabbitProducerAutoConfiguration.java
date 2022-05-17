package com.fengxuechao.rabbit.producer.autoconfigure;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.fengxuechao.rabbit.task.annotation.EnableElasticJob;

/**
 * 	$RabbitProducerAutoConfiguration 自动装配 
 * @author fengxuechao
 *
 */
@EnableElasticJob
@Configuration
@ComponentScan({"com.fengxuechao.rabbit.producer.*"})
public class RabbitProducerAutoConfiguration {


}
