package ru.vsu.tgbot.components.bot;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vsu.tgbot.services.business.QueryService;

@Component
@RequiredArgsConstructor
public class TgUpdateConsumer {
    private final QueryService queryService;

    public void consume(Update update) {
        queryService.processQuery(update);
    }
}
