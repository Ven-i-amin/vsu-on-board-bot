package ru.vsu.tgbot.services.core;

import ru.vsu.tgbot.model.response.GroupResponseDto;

import java.util.List;

public interface GroupService {
    GroupResponseDto getQuestionGroup(String groupId, String language);
    GroupResponseDto getGroupWithRecursion(String groupId, Integer recursion, String language);
    List<GroupResponseDto> getInnerGroups(String thisGroupId, String language);
    List<GroupResponseDto> getInnerGroupsForEachGroup(List<String> groupIds, String language);
    GroupResponseDto getStartGroup(String language);
    GroupResponseDto getStartGroup();
}
