package com.fengxuechao.esjob.task.test;

import org.springframework.stereotype.Component;

import com.fengxuechao.rabbit.task.annotation.ElasticJobConfig;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;

@Component
@ElasticJobConfig(
			name = "com.fengxuechao.esjob.task.test.TestJob",
			cron = "0/5 * * * * ?",
			description = "测试定时任务",
			overwrite = true,
			shardingTotalCount = 5
		)
public class TestJob implements SimpleJob {

	@Override
	public void execute(ShardingContext shardingContext) {
		System.err.println("执行Test job.");
	}

}
