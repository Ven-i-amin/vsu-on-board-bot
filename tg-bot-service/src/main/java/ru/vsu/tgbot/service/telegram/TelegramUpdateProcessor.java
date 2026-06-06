package ru.vsu.tgbot.service.telegram;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vsu.tgbot.services.business.QueryService;

@Component
public class TelegramUpdateProcessor {
    private final QueryService queryService;

    public TelegramUpdateProcessor(QueryService queryService) {
        this.queryService = queryService;
    }

    public void process(Update update) {
        queryService.processQuery(update);
    }
}
