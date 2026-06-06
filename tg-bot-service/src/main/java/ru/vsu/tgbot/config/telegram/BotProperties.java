package ru.vsu.tgbot.config.telegram;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "telegram.bot")
public class BotProperties {
    private String mode = "long-polling";
    private String username;
    private String token;
    private String webhookPath = "/telegram/webhook";
    private String webhookUrl;
    private String webhookSecret;
}
