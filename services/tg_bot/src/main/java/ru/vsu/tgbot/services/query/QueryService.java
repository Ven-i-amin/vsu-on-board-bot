package ru.vsu.tgbot.services.query;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface QueryService {
    SendMessage processQuery(Update update);
}
