package com.weyland.yutani.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weyland.yutani.core.services.AuditService;
import com.weyland.yutani.core.services.CommandQueueService;
import com.weyland.yutani.core.services.ErrorHandlerService;
import com.weyland.yutani.core.services.MetricsService;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@ComponentScan("com.weyland.yutani.core.services")
public class StarterConfiguration {

    @Bean
    public CommandQueueService commandQueueService(
            @Value("${command.queue.max-size:10}") int maxQueueSize,
            @Value("${command.queue.processing-delay-ms:10000}") long processingDelayMs,
            MetricsService metricsService) {
        return new CommandQueueService(maxQueueSize, processingDelayMs, metricsService);
    }

    @Bean
    public ErrorHandlerService errorHandlerService() {
        return new ErrorHandlerService();
    }


    @Bean
    public MetricsService metricsService(MeterRegistry registry) {
        return new MetricsService(registry);
    }

    @Bean
    public AuditService auditService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        return new AuditService(kafkaTemplate, objectMapper);
    }
}
