package ru.vsu.tgbot.services.business;

import ru.vsu.tgbot.model.dto.GroupDto;

import java.util.List;

public interface GroupCacheService {
    GroupDto getStartGroup();
    GroupDto getGroup(String groupName, String language);
    List<GroupDto> hydrateGroupPath(List<GroupDto> persistedGroupPath, String language);
}
