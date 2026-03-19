package ru.vsu.tgbot.model.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record GroupDto(
        String groupId,
        String title,
        String parentId,
        List<GroupDto> innerGroups,
        List<QuestionDto> questions
) {
}
