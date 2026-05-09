package ru.vsu.tgbot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AppConfiguration {
    @Bean(destroyMethod = "shutdown")
    public ExecutorService sessionPatchExecutor() {
        return Executors.newFixedThreadPool(Math.max(2, Runtime.getRuntime().availableProcessors()));
    }
}
