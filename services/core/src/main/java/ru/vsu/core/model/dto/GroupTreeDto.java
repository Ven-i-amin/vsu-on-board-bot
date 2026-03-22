package ru.vsu.core.model.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record GroupLocalizedDto(
        String groupId,
        String title,
        GroupLocalizedDto parentId,
        List<GroupLocalizedDto> innerGroups,
        List<QuestionLocalizedDto> questions
) {
}
