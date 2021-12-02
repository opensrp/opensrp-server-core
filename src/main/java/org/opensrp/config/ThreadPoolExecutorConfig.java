package org.opensrp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadPoolExecutorConfig {

	@Value("#{opensrp['thread.pool.core.size'] ?: 5 }")
	private int threadPoolCoreSize;

	@Value("#{opensrp['thread.pool.max.size'] ?: 10 }")
	private int threadPoolMaxSize;

	@Value("#{opensrp['thread.pool.queue.capacity'] ?: 25 }")
	private int threadPoolQueueCapacity;

	@Bean
	public ThreadPoolTaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setCorePoolSize(threadPoolCoreSize);
		threadPoolTaskExecutor.setMaxPoolSize(threadPoolMaxSize);
		threadPoolTaskExecutor.setQueueCapacity(threadPoolQueueCapacity);
		return threadPoolTaskExecutor;
	}

}
