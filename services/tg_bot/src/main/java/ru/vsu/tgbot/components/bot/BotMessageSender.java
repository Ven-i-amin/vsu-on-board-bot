package ru.vsu.tgbot.components.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotMessageSender {
    private static final String HTML_PARSE_MODE = "HTML";

    private final TelegramClient telegramClient;

    public Message send(SendMessage message) {
        if (message == null) {
            return null;
        }

        if (message.getParseMode() == null) {
            message.setParseMode(HTML_PARSE_MODE);
        }

        Message sendedMessage = null;

        try {
            sendedMessage = telegramClient.execute(message);
        } catch (Exception e) {
            log.error("Failed to send Telegram message", e);
        }

        return sendedMessage;
    }

    public void delete(DeleteMessage message) {
        if (message == null) {
            return;
        }

        try {
            telegramClient.execute(message);
        } catch (Exception e) {
            log.error("Failed to delete Telegram message", e);
        }
    }
}
