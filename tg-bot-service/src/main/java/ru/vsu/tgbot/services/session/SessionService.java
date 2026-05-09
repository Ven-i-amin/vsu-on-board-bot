package ru.vsu.tgbot.services.session;

import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.model.dto.GroupDto;

import java.util.List;

public interface SessionService {
    void saveSession(SessionDto session);
    SessionDto getSession(Long chatId);
    void deleteSession(Long chatId);
    void patchSessionByGroupPath(Long chatId, List<GroupDto> groupPath);
}
