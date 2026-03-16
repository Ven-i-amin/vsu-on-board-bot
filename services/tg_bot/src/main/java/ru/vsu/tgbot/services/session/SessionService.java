package ru.vsu.tgbot.services.session;

import ru.vsu.tgbot.model.SessionInfo;

public interface SessionService {
    void saveSession(Long id, SessionInfo session);
    SessionInfo getSession(Long chatId);
}
