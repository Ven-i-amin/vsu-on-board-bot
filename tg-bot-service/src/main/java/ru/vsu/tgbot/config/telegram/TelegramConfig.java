package ru.vsu.tgbot.config.telegram;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({BotProperties.class, ProxyProperties.class})
public class TelegramConfig {
}
