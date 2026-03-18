package ru.vsu.tgbot.services.session;

import ru.vsu.tgbot.model.entity.Session;

public interface SessionService {
    void saveSession(Session session);
    Session getSession(Long chatId);
}
