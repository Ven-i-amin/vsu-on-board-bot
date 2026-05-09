package ru.vsu.tgbot.service.telegram;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.methods.updates.DeleteWebhook;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.vsu.tgbot.config.telegram.BotProperties;

@Slf4j
@Component
@ConditionalOnProperty(name = "telegram.bot.mode", havingValue = "webhook")
public class TelegramWebhookRegistrar {
    private final TelegramClient telegramClient;
    private final BotProperties botProperties;

    public TelegramWebhookRegistrar(TelegramClient telegramClient, BotProperties botProperties) {
        this.telegramClient = telegramClient;
        this.botProperties = botProperties;
    }

    @PostConstruct
    public void registerWebhook() {
        if (!StringUtils.hasText(botProperties.getWebhookUrl())) {
            log.warn("Skipping Telegram webhook registration because telegram.bot.webhook-url is empty");
            return;
        }

        try {
            telegramClient.execute(new DeleteWebhook());
        } catch (TelegramApiException exception) {
            log.info("Error deleting webhook before registration", exception);
        }

        try {
            SetWebhook.SetWebhookBuilder webhookBuilder = SetWebhook.builder()
                .url(botProperties.getWebhookUrl());
            if (StringUtils.hasText(botProperties.getWebhookSecret())) {
                webhookBuilder.secretToken(botProperties.getWebhookSecret());
            }
            telegramClient.execute(webhookBuilder.build());
        } catch (TelegramApiException exception) {
            log.info("Error setting webhook", exception);
        }
    }
}
