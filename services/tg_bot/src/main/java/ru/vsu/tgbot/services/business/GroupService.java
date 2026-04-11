package ru.vsu.tgbot.services.business;

import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.model.dto.GroupDto;

public interface GroupService {
    void moveForward(SessionDto sessionDto, GroupDto newGroup);
    void moveBackward(SessionDto sessionDto);
    void moveToStart(SessionDto sessionDto);

    void removeLastGroup(SessionDto sessionDto);
}
