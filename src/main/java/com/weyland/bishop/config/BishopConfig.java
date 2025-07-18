package com.weyland.bishop.config;

import com.weyland.yutani.core.services.CommandQueueService;
import com.weyland.yutani.core.services.CommandService;
import com.weyland.yutani.core.services.MetricsService;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Slf4j
@Configuration
public class BishopConfig {

    @Bean
    public CommandQueueService commandQueueService(MetricsService metricsService) {
        return new CommandQueueService(100, 100, metricsService);
    }

    @Bean
    public MetricsService metricsService(MeterRegistry meterRegistry) {
        return new MetricsService(meterRegistry);
    }

    @Bean
    public CommandService commandService(CommandQueueService commandQueueService, MetricsService metricsService) {
        return new CommandService(commandQueueService, metricsService);
    }

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("Bishop-");
        executor.initialize();
        return executor;
    }
}