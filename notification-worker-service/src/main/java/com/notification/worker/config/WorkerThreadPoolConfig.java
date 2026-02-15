package com.notification.worker.config;

import org.springframework.context.annotation.*;
import java.util.concurrent.*;

@Configuration
public class WorkerThreadPoolConfig {

    @Bean
    public ExecutorService notificationExecutor() {
        return new ThreadPoolExecutor(
                2,   // core
                5,   // max
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100)
        );
    }
}
