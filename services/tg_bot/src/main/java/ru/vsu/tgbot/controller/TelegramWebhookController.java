package ru.vsu.tgbot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vsu.tgbot.components.bot.TgUpdateConsumer;
import ru.vsu.tgbot.config.TelegramBotProperties;

@RestController
@RequiredArgsConstructor
public class TelegramWebhookController {
    private static final String TELEGRAM_SECRET_HEADER = "X-Telegram-Bot-Api-Secret-Token";

    private final TgUpdateConsumer updateConsumer;
    private final TelegramBotProperties properties;

    @PostMapping("${telegram.bot.webhook-path:/telegram/webhook}")
    public ResponseEntity<Void> onUpdate(
            @RequestBody Update update,
            @RequestHeader(name = TELEGRAM_SECRET_HEADER, required = false) String secretToken
    ) {
        if (StringUtils.hasText(properties.secretToken()) && !properties.secretToken().equals(secretToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        updateConsumer.consume(update);
        return ResponseEntity.ok().build();
    }
}
