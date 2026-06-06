package ru.vsu.tgbot.services.session;

import ru.vsu.tgbot.model.dto.SessionDto;

public interface SessionService {
    void saveSession(SessionDto session);
    SessionDto getSession(Long chatId);
    void deleteSession(Long chatId);
}
