package ru.vsu.tgbot.model.response;

import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;

@Data
@Builder
public class QuestionResponseDto {
    @Id
    private String questionId;
    private String name;
    @NotNull
    private GroupResponseDto parent;
    @NotNull
    private String title;
    @NotNull
    private String text;
}
