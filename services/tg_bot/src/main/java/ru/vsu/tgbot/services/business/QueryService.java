package ru.vsu.tgbot.services.business;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface QueryService {
    void processQuery(Update update);
}
