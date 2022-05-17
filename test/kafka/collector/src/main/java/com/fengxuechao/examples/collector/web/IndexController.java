package com.fengxuechao.examples.collector.web;

import com.fengxuechao.examples.collector.util.InputMDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
public class IndexController {

	private final Logger log = LoggerFactory.getLogger(getClass());

	
	/**
	 * log4j2 日志输出格式
	 *
	 * [%d{yyyy-MM-dd'T'HH:mm:ss.SSSZZ}]
	 * [%level{length=5}] 
	 * [%thread-%tid] 
	 * [%logger] 
	 * [%X{hostName}] 
	 * [%X{ip}] 
	 * [%X{applicationName}] 
	 * [%F,%L,%C,%M] 
	 * [%m] ## '%ex'%n
	 * -----------------------------------------------
	 * [2019-09-18T14:42:51.451+08:00]
	 * [INFO] 
	 * [main-1] 
	 * [org.springframework.boot.web.embedded.tomcat.TomcatWebServer]
	 * [] 
	 * [] 
	 * [] 
	 * [TomcatWebServer.java,90,org.springframework.boot.web.embedded.tomcat.TomcatWebServer,initialize] 
	 * [Tomcat initialized with port(s): 8001 (http)] ## ''
	 *
	 * logstash grok 表达式
	 * * NOTSPACE 表示不能为空，例如 currentDateTime 不能为空
	 * * DATA 则表示数据可以为空
	 *
	 * ["message", 
	 * "\[%{NOTSPACE:currentDateTime}\] 
	 *  \[%{NOTSPACE:level}\] 
	 *  \[%{NOTSPACE:thread-id}\] 
	 *  \[%{NOTSPACE:class}\] 
	 *  \[%{DATA:hostName}\] 
	 *  \[%{DATA:ip}\] 
	 *  \[%{DATA:applicationName}\]
	 *  \[%{DATA:location}\] 
	 *  \[%{DATA:messageInfo}\] 
	 *  ## (\'\'|%{QUOTEDSTRING:throwable})"]
	 * @return
	 */
	@RequestMapping(value = "/index")
	public String index() {
		InputMDC.putMDC();
		
		log.info("我是一条info日志");
		
		log.warn("我是一条warn日志");

		log.error("我是一条error日志");
		
		return "idx";
	}
	
	
	@RequestMapping(value = "/err")
	public String err() {
		InputMDC.putMDC();
		try {
			int a = 1/0;
		} catch (Exception e) {
			log.error("算术异常", e);
		}
		return "err";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
