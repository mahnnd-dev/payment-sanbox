package com.neo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig {

    @Value("${async.thread-pool.core-size:5}")
    private int corePoolSize;

    @Value("${async.thread-pool.max-size:10}")
    private int maxPoolSize;

    @Value("${async.thread-pool.queue-capacity:100}")
    private int queueCapacity;

    @Value("${async.thread-pool.thread-name-prefix:neo-async-}")
    private String threadNamePrefix;

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();
        log.info("Async task executor initialized - core: {}, max: {}, queue: {}",
                corePoolSize, maxPoolSize, queueCapacity);
        return executor;
    }
}
