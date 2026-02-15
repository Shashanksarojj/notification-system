package com.notification.worker.config;


import io.micrometer.core.instrument.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public Counter sentCounter(MeterRegistry registry) {
        return registry.counter("notifications.sent");
    }

    @Bean
    public Counter failedCounter(MeterRegistry registry) {
        return registry.counter("notifications.failed");
    }

    @Bean
    public Counter retryCounter(MeterRegistry registry) {
        return registry.counter("notifications.retried");
    }
}
