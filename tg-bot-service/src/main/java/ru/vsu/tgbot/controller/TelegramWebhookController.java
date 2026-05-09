package ru.vsu.tgbot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vsu.tgbot.config.telegram.BotProperties;
import ru.vsu.tgbot.service.telegram.TelegramUpdateProcessor;

@Slf4j
@RestController
@ConditionalOnProperty(name = "telegram.bot.mode", havingValue = "webhook")
public class TelegramWebhookController {
    private static final String TELEGRAM_SECRET_HEADER = "X-Telegram-Bot-Api-Secret-Token";

    private final TelegramUpdateProcessor updateProcessor;
    private final BotProperties botProperties;
    private final String webhookPath;

    public TelegramWebhookController(
        TelegramUpdateProcessor updateProcessor,
        BotProperties botProperties,
        @Qualifier("telegramWebhookPath") String webhookPath
    ) {
        this.updateProcessor = updateProcessor;
        this.botProperties = botProperties;
        this.webhookPath = webhookPath;
    }

    @PostMapping("${telegram.bot.webhook-path:/telegram/webhook}")
    public ResponseEntity<Void> onUpdate(
        @RequestBody Update update,
        @RequestHeader(name = TELEGRAM_SECRET_HEADER, required = false) String secretToken
    ) {
        if (StringUtils.hasText(botProperties.getWebhookSecret()) && !botProperties.getWebhookSecret().equals(secretToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            updateProcessor.process(update);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            log.error("Failed to process Telegram webhook update on path {}", webhookPath, exception);
            return ResponseEntity.internalServerError().build();
        }
    }
}
