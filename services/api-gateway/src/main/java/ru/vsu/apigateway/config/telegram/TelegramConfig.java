package ru.vsu.apigateway.config.telegram;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({BotProperties.class, TgBotForwardProperties.class, ProxyProperties.class})
public class TelegramConfig {
}
