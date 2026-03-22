package ru.vsu.tgbot.model.response;


import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.Map;

@Builder
public record GroupResponseDto(
        @Id String groupId,
        @NotNull @NotEmpty Map<String, String> title,
        String parentId,
        List<GroupResponseDto> innerGroups,
        List<QuestionResponseDto> questions
) {
}
