package ru.vsu.tgbot.components.bot;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vsu.tgbot.services.business.QueryService;

@Slf4j
@Component
@ConditionalOnProperty(name = "telegram.bot.mode", havingValue = "long-polling")
@AllArgsConstructor
public class LongPollingHandler implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    private final String telegramToken;
    private final QueryService queryService;

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
        queryService.processQuery(update);
    }
}
