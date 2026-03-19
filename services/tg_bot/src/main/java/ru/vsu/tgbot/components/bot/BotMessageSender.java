package ru.vsu.tgbot.components.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotMessageSender {
    private final TelegramClient telegramClient;

    public void send(SendMessage message) {
        if (message == null) {
            return;
        }

        try {
            telegramClient.execute(message);
        } catch (Exception e) {
            log.error("Failed to send Telegram message", e);
        }
    }
}
