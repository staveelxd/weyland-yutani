package com.weyland.yutani.core.config;


import io.micrometer.core.instrument.MeterRegistry;
import com.weyland.yutani.core.services.CommandQueueService;
import com.weyland.yutani.core.services.ErrorHandlerService;
import com.weyland.yutani.core.services.MetricsService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
            @Value("${command.queue.max-size:100}") int maxQueueSize,
            @Value("${command.queue.processing-delay-ms:100}") long processingDelayMs,
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
}
