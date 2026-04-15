package ru.vsu.tgbot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GatewayClientConfig {
    @Bean
    public WebClient gatewayTelegramClient(
            @Value("${gateway.base-url:http://localhost:443}") String gatewayBaseUrl,
            @Value("${gateway.internal.token:}") String internalToken
    ) {
        return WebClient.builder()
                .baseUrl(gatewayBaseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .defaultHeader("X-Internal-Bot-Token", internalToken == null ? "" : internalToken)
                .build();
    }
}
