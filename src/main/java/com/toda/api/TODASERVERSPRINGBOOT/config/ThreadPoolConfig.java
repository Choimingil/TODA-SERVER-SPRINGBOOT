package com.toda.api.TODASERVERSPRINGBOOT.config;

import com.toda.api.TODASERVERSPRINGBOOT.decorators.CustomTaskDecorator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
@RequiredArgsConstructor
public class ThreadPoolConfig {
    private final CustomTaskDecorator customTaskDecorator;
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

        executor.setTaskDecorator(customTaskDecorator);
        executor.setThreadNamePrefix("async-task-");
        executor.setThreadGroupName("async-group");

        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }
}

