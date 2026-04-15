package ru.vsu.apigateway.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updates.DeleteWebhook;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.telegram.telegrambots.webhook.TelegramWebhookBot;

@Slf4j
@Component
@ConditionalOnProperty(name = "telegram.bot.mode", havingValue = "webhook")
@AllArgsConstructor
public class WebhookHandler implements TelegramWebhookBot {
    private final TelegramClient telegramClient;
    private final SetWebhook webhook;

    @Qualifier("usernamePath")
    private final String usernamePath;

    private final TgBotUpdateForwarder updateForwarder;

    @Override
    public void runDeleteWebhook() {
        try {
            telegramClient.execute(new DeleteWebhook());
        } catch (TelegramApiException exception) {
            log.info("Error deleting webhook", exception);
        }
    }

    @Override
    public void runSetWebhook() {
        try {
            telegramClient.execute(webhook);
        } catch (TelegramApiException exception) {
            log.info("Error setting webhook", exception);
        }
    }

    @Override
    public BotApiMethod<?> consumeUpdate(Update update) {
        try {
            updateForwarder.forward(update).block();
        } catch (Exception exception) {
            log.error("Failed to forward Telegram webhook update", exception);
        }

        return null;
    }

    @Override
    public String getBotPath() {
        return usernamePath;
    }
}
