package ru.vsu.tgbot.services.core;

import ru.vsu.contract.model.response.GroupResponseDto;

import java.util.List;

public interface GroupClientWithoutTree {
    GroupResponseDto getQuestionGroup(String groupName, String language);
    GroupResponseDto getGroupWithDepth(String groupName, Integer depth, String language);
    List<GroupResponseDto> getGroupsByNameWithDepth(String groupName, Integer depth, String language);
    List<GroupResponseDto> getInnerGroups(String groupName, String language);
    List<GroupResponseDto> getInnerGroupsForEachGroup(List<String> groupNames, String language);
    GroupResponseDto getStartGroup();
}
