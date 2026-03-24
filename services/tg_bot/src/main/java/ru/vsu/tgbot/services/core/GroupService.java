package ru.vsu.tgbot.services.core;

import ru.vsu.tgbot.model.dto.GroupDto;

import java.util.List;

public interface GroupService {
    GroupDto getQuestionGroup(String groupName, String language);
    GroupDto getGroupWithDepth(String groupName, Integer depth, String language);
    GroupDto getGroupByNameWithDepth(String groupName, Integer depth, String language);
    List<GroupDto> getInnerGroups(String groupName, String language);
    List<GroupDto> getInnerGroupsForEachGroup(List<String> groupNames, String language);
    GroupDto getStartGroup();
}
