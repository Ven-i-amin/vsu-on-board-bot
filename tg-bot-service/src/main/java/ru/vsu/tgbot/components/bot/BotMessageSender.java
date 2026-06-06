package ru.vsu.tgbot.components.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class BotMessageSender {
    private static final String HTML_PARSE_MODE = "HTML";
    private static final Logger log = LoggerFactory.getLogger(BotMessageSender.class);

    private final TelegramClient telegramClient;

    public BotMessageSender(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    public Integer send(SendMessage message) {
        if (message == null) {
            return null;
        }

        if (message.getParseMode() == null) {
            message.setParseMode(HTML_PARSE_MODE);
        }

        Integer messageId = null;

        try {
            messageId = telegramClient.execute(message).getMessageId();
        } catch (TelegramApiException e) {
            log.error("Failed to send Telegram message", e);
        }

        return messageId;
    }

    public void sendDocument(SendDocument document) {
        if (document == null) {
            return;
        }

        try {
            telegramClient.execute(document);
        } catch (TelegramApiException e) {
            log.error("Failed to send Telegram document", e);
        }
    }

    public void sendMediaGroup(SendMediaGroup mediaGroup) {
        if (mediaGroup == null) {
            return;
        }

        try {
            telegramClient.execute(mediaGroup);
        } catch (TelegramApiException e) {
            log.error("Failed to send Telegram media group", e);
        }
    }

    public void delete(DeleteMessage message) {
        if (message == null) {
            return;
        }

        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            log.error("Failed to delete Telegram message", e);
        }
    }
}
