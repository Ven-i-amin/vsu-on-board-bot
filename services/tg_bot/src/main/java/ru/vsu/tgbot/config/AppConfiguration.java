package ru.vsu.tgbot.config;

import org.springframework.beans.factory.annotation.Value;
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

    @Bean(name = "internalToken")
    public String internalToken(@Value("${telegram.internal.token:}") String internalToken) {
        return internalToken;
    }
}
