package com.neo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neo.dto.IPNRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class BeanConfig {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public BlockingQueue<IPNRequest> requestBlockingQueue(@Value("${app.queue.capacity:1000}") int queueCapacity) {
        return new LinkedBlockingQueue<>(queueCapacity);
    }
}
