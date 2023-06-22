package com.toda.api.TODASERVERSPRINGBOOT.utils.config;

import com.toda.api.TODASERVERSPRINGBOOT.utils.decorators.CustomTaskDecorator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class ThreadPoolConfig {
    @Value("${spring.task.execution.pool.core-size}")
    int CORE_POOL_SIZE;
    @Value("${spring.task.execution.pool.max-size}")
    int MAX_POOL_SIZE;
    @Value("${spring.task.execution.pool.queue-capacity}")
    int QUEUE_CAPACITY;

    @Bean
    public ThreadPoolTaskExecutor taskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(CORE_POOL_SIZE);
        executor.setMaxPoolSize(MAX_POOL_SIZE);
        executor.setQueueCapacity(QUEUE_CAPACITY);

        executor.setTaskDecorator(new CustomTaskDecorator());
        executor.setThreadNamePrefix("async-task-");
        executor.setThreadGroupName("async-group");

        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }
}

