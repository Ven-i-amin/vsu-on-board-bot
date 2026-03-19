package ru.vsu.core.model.response;

import java.util.List;

public record GroupResponseDto(
        String groupId,
        String title,
        String parentId,
        List<GroupResponseDto> innerGroups,
        List<QuestionResponseDto> questions
) {
}
