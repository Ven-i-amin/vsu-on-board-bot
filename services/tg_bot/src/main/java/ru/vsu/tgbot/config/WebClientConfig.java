package ru.vsu.tgbot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean(name = "coreClient")
    public WebClient coreClient() {
        return WebClient
                .builder()
                .baseUrl("http://core:8081")
                .build();
    }
}
