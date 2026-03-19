package ru.vsu.tgbot.services.business;

import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.model.response.GroupResponseDto;

public interface GroupWindowService {
    void moveForward(SessionDto sessionDto, GroupResponseDto newGroup);
    void moveBackward(SessionDto sessionDto);
    void moveToStart(SessionDto sessionDto);
}
