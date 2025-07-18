package com.weyland.bishop.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;

public class KafkaTopicConfig {
    @Bean
    public NewTopic weatherTopic() {
        return new NewTopic("audit", 1, (short) 1);
    }
}