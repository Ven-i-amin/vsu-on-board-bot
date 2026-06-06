package ru.vsu.tgbot.services.core;

import ru.vsu.tgbot.model.dto.GroupDto;
import ru.vsu.tgbot.model.dto.QuestionDto;

import java.util.List;

public interface GroupService {
    GroupDto getRootGroup();
    GroupDto getGroup(String groupName);
    List<GroupDto> getGroupChildren(String groupName);
    List<QuestionDto> getGroupQuestions(String groupName);

    /** Convenience: fetch group + direct children + questions in one call. */
    GroupDto getGroupWithContent(String groupName);

    /** Convenience: fetch root group + its direct children + questions. */
    GroupDto getRootGroupWithContent();
}
