package ru.vsu.tgbot.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "telegram.bot")
public record TelegramBotProperties(
        @NotBlank String token,
        @Pattern(regexp = "webhook|long-polling") 
        @DefaultValue("long-polling") String mode,
        String webhookUrl,
        @DefaultValue("/telegram/webhook") String webhookPath,
        String secretToken,
        @DefaultValue("true") boolean autoRegister,
        @DefaultValue("30") int pollingTimeoutSeconds
) {
}
