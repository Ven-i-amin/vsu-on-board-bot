package ru.vsu.tgbot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Value("${core.base-url:http://localhost:8081}")
    private String coreBaseUrl;

    @Bean(name = "coreClient")
    public WebClient coreClient() {
        return WebClient
                .builder()
                .baseUrl(coreBaseUrl)
                .build();
    }
}
