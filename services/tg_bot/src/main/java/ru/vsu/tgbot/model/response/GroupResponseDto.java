package ru.vsu.tgbot.model.response;


import lombok.Builder;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;

import java.util.List;

@Builder
public record GroupResponseDto(
        @Id String groupId,
        @NotNull String title,
        String parentId,
        List<GroupResponseDto> innerGroups,
        List<QuestionResponseDto> questions
) {
}
