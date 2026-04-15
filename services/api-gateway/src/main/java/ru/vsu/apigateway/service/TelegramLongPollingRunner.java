package ru.vsu.apigateway.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
@ConditionalOnProperty(name = "telegram.bot.mode", havingValue = "long-polling")
@AllArgsConstructor
public class TelegramLongPollingRunner implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    @Qualifier("telegramToken")
    private final String telegramToken;
    private final TgBotUpdateForwarder updateForwarder;

    @Override
    public String getBotToken() {
        return telegramToken;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        try {
            updateForwarder.forward(update).block();
        } catch (Exception exception) {
            log.error("Failed to forward Telegram update from long polling", exception);
        }
    }
}
