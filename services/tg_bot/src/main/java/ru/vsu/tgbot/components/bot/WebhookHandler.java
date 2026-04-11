package ru.vsu.tgbot.components.bot;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.DeleteWebhook;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.telegram.telegrambots.webhook.TelegramWebhookBot;
import ru.vsu.tgbot.services.business.QueryService;

@Component
@ConditionalOnProperty(name = "telegram.bot.mode", havingValue = "webhook")
@Slf4j
@AllArgsConstructor
public class WebhookHandler implements TelegramWebhookBot {
    private TelegramClient telegramClient;
    private SetWebhook webhook;
    private String usernamePath;

    private QueryService queryService;

    @Override
    public void runDeleteWebhook() {
        try {
            telegramClient.execute(new DeleteWebhook());
        } catch (TelegramApiException e) {
            log.info("Error deleting webhook");
        }
    }

    @Override
    public void runSetWebhook() {
        try {
            telegramClient.execute(webhook);
        } catch (TelegramApiException e) {
            log.info("Error setting webhook");
        }
    }

    @Override
    public BotApiMethod<?> consumeUpdate(Update update) {
        queryService.processQuery(update);
        return null;
    }

    @Override
    public String getBotPath() {
        return usernamePath;
    }
}
