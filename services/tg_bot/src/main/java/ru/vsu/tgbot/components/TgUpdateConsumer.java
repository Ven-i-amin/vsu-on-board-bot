package ru.vsu.tgbot.components;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.vsu.tgbot.services.query.QueryService;

@Slf4j
@Component
@RequiredArgsConstructor
public class TgUpdateConsumer implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    private final QueryService queryService;

    @Override
    public void consume(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        SendMessage message = queryService.processQuery(update);

        try {
            telegramClient.execute(message);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }
}
