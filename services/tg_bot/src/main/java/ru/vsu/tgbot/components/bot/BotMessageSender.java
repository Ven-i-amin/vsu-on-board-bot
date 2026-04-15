package ru.vsu.tgbot.components.bot;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Component
@RequiredArgsConstructor
public class BotMessageSender {
    private static final String HTML_PARSE_MODE = "HTML";
    private static final Logger log = LoggerFactory.getLogger(BotMessageSender.class);

    private final WebClient gatewayTelegramClient;

    public Message send(SendMessage message) {
        if (message == null) {
            return null;
        }

        if (message.getParseMode() == null) {
            message.setParseMode(HTML_PARSE_MODE);
        }

        Message sendedMessage = null;

        try {
            sendedMessage = gatewayTelegramClient.post()
                    .uri("/internal/telegram/messages/send")
                    .bodyValue(message)
                    .retrieve()
                    .bodyToMono(Message.class)
                    .block();
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
            gatewayTelegramClient.post()
                    .uri("/internal/telegram/messages/delete")
                    .bodyValue(message)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            log.error("Failed to delete Telegram message", e);
        }
    }
}
