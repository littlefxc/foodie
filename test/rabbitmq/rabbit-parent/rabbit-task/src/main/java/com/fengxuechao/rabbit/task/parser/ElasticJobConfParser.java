package com.fengxuechao.rabbit.task.parser;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fengxuechao.rabbit.task.enums.ElasticJobTypeEnum;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.util.StringUtils;

import com.fengxuechao.rabbit.task.annotation.ElasticJobConfig;
import com.fengxuechao.rabbit.task.autoconfigure.JobZookeeperProperties;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.JobTypeConfiguration;
import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.config.script.ScriptJobConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.rdb.JobEventRdbConfiguration;
import com.dangdang.ddframe.job.executor.handler.JobProperties;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;

import lombok.extern.slf4j.Slf4j;

/**
 * 1. IOC 容器准备好之后，找到 @ElasticJobConfig 注解的类
 * 2. 根据注解内容创建分布式任务bean
 * 3. 分布式任务bean 交由 IOC 管理
 * 4. 启动分布式任务
 */
@Slf4j
public class ElasticJobConfParser implements ApplicationListener<ApplicationReadyEvent> {

	private JobZookeeperProperties jobZookeeperProperties;
	
	private ZookeeperRegistryCenter zookeeperRegistryCenter;
	
	public ElasticJobConfParser(JobZookeeperProperties jobZookeeperProperties,
			ZookeeperRegistryCenter zookeeperRegistryCenter) {
		this.jobZookeeperProperties = jobZookeeperProperties;
		this.zookeeperRegistryCenter = zookeeperRegistryCenter;
	}

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		try {
			ApplicationContext applicationContext = event.getApplicationContext();
			Map<String, Object> beanMap = applicationContext.getBeansWithAnnotation(ElasticJobConfig.class);
			for(Iterator<?> it = beanMap.values().iterator(); it.hasNext();) {
				Object confBean = it.next();
				Class<?> clazz = confBean.getClass();
				if(clazz.getName().indexOf("$") > 0) {
					String className = clazz.getName();
					clazz = Class.forName(className.substring(0, className.indexOf("$")));
				}
				// 	获取接口类型 用于判断是什么类型的任务
				String jobTypeName = clazz.getInterfaces()[0].getSimpleName();
				//	获取配置项 ElasticJobConfig
				ElasticJobConfig conf = clazz.getAnnotation(ElasticJobConfig.class);
				
				String jobClass = clazz.getName();
				String jobName = this.jobZookeeperProperties.getNamespace() + "." + conf.name();

				String eventTraceRdbDataSource = conf.eventTraceRdbDataSource();
				
				//	先把当当网的esjob的相关configuration
				JobCoreConfiguration coreConfig = JobCoreConfiguration
						.newBuilder(jobName, conf.cron(), conf.shardingTotalCount())
						.shardingItemParameters(conf.shardingItemParameters())
						.description(conf.description())
						.failover(conf.failover())
						.jobParameter(conf.jobParameter())
						.misfire(conf.misfire())
						.jobProperties(JobProperties.JobPropertiesEnum.JOB_EXCEPTION_HANDLER.getKey(), conf.jobExceptionHandler())
						.jobProperties(JobProperties.JobPropertiesEnum.EXECUTOR_SERVICE_HANDLER.getKey(), conf.executorServiceHandler())
						.build();
				
				//	到底要创建什么样的任务.
				JobTypeConfiguration typeConfig = null;
				if(ElasticJobTypeEnum.SIMPLE.getType().equals(jobTypeName)) {
					typeConfig = new SimpleJobConfiguration(coreConfig, jobClass);
				}
				
				if(ElasticJobTypeEnum.DATAFLOW.getType().equals(jobTypeName)) {
					typeConfig = new DataflowJobConfiguration(coreConfig, jobClass, conf.streamingProcess());
				}
				
				if(ElasticJobTypeEnum.SCRIPT.getType().equals(jobTypeName)) {
					typeConfig = new ScriptJobConfiguration(coreConfig, conf.scriptCommandLine());
				}
				
				// LiteJobConfiguration
				LiteJobConfiguration jobConfig = LiteJobConfiguration
						.newBuilder(typeConfig)
						.overwrite(conf.overwrite())
						.disabled(conf.disabled())
						.monitorPort(conf.monitorPort())
						.monitorExecution(conf.monitorExecution())
						.maxTimeDiffSeconds(conf.maxTimeDiffSeconds())
						.jobShardingStrategyClass(conf.jobShardingStrategyClass())
						.reconcileIntervalMinutes(conf.reconcileIntervalMinutes())
						.build();
				
				// 	创建一个Spring的beanDefinition
				BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(SpringJobScheduler.class);
				factory.setInitMethodName("init");
				factory.setScope("prototype");
				
				//	1.添加bean构造参数，相当于添加自己的真实的任务实现类
				if (!ElasticJobTypeEnum.SCRIPT.getType().equals(jobTypeName)) {
					factory.addConstructorArgValue(confBean);
				}
				//	2.添加注册中心
				factory.addConstructorArgValue(this.zookeeperRegistryCenter);
				//	3.添加LiteJobConfiguration
				factory.addConstructorArgValue(jobConfig);

				//	4.如果有eventTraceRdbDataSource 则也进行添加
				if (StringUtils.hasText(eventTraceRdbDataSource)) {
					BeanDefinitionBuilder rdbFactory = BeanDefinitionBuilder
							.rootBeanDefinition(JobEventRdbConfiguration.class);
					rdbFactory.addConstructorArgReference(eventTraceRdbDataSource);
					factory.addConstructorArgValue(rdbFactory.getBeanDefinition());
				}
				
				//  5.添加监听
				List<?> elasticJobListeners = getTargetElasticJobListeners(conf);
				factory.addConstructorArgValue(elasticJobListeners);
				
				// 	接下来就是把factory 也就是 SpringJobScheduler注入到Spring容器中
				DefaultListableBeanFactory defaultListableBeanFactory =
						(DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();

				String registerBeanName = conf.name() + "SpringJobScheduler";
				defaultListableBeanFactory.registerBeanDefinition(registerBeanName, factory.getBeanDefinition());
				SpringJobScheduler scheduler = (SpringJobScheduler)applicationContext.getBean(registerBeanName);
				scheduler.init();
				log.info("启动elastic-job作业: " + jobName);
			}
			log.info("共计启动elastic-job作业数量为: {} 个", beanMap.values().size());
			
		} catch (Exception e) {
			log.error("elasticjob 启动异常, 系统强制退出", e);
			System.exit(1);
		}
	}
	
	private List<BeanDefinition> getTargetElasticJobListeners(ElasticJobConfig conf) {
		List<BeanDefinition> result = new ManagedList<BeanDefinition>(2);
		String listeners = conf.listener();
		if (StringUtils.hasText(listeners)) {
			BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(listeners);
			factory.setScope("prototype");
			result.add(factory.getBeanDefinition());
		}

		String distributedListeners = conf.distributedListener();
		long startedTimeoutMilliseconds = conf.startedTimeoutMilliseconds();
		long completedTimeoutMilliseconds = conf.completedTimeoutMilliseconds();

		if (StringUtils.hasText(distributedListeners)) {
			BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(distributedListeners);
			factory.setScope("prototype");
			factory.addConstructorArgValue(Long.valueOf(startedTimeoutMilliseconds));
			factory.addConstructorArgValue(Long.valueOf(completedTimeoutMilliseconds));
			result.add(factory.getBeanDefinition());
		}
		return result;
	}

}
