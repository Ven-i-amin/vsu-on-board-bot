package ru.vsu.tgbot.services.core;

import ru.vsu.tgbot.model.dto.GroupDto;

import java.util.List;

public interface GroupService {
    GroupDto getQuestionGroup(String groupId, String language);
    GroupDto getGroupWithDepth(String groupId, Integer depth, String language);
    List<GroupDto> getInnerGroups(String thisGroupId, String language);
    List<GroupDto> getInnerGroupsForEachGroup(List<String> groupIds, String language);
    GroupDto getStartGroup(String language);
    GroupDto getStartGroup();
}
