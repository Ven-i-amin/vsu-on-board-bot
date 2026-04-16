package ru.vsu.tgbot.services.business;

import ru.vsu.contract.model.response.GroupResponseDto;
import ru.vsu.tgbot.model.dto.SessionDto;

public interface GroupServiceWithoutTree {
    void addGroupToPath(SessionDto sessionDto, String groupName);
    void removeGroupFromPath(SessionDto sessionDto, String groupName);
}
