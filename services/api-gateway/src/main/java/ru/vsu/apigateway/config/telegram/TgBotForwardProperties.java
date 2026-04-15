package ru.vsu.apigateway.config.telegram;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "telegram.tg-bot")
public class TgBotForwardProperties {
    private String updateUri = "http://localhost:8082/internal/telegram/updates";
    private String internalToken;
}
