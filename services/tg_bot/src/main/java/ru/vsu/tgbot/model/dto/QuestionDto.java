package ru.vsu.tgbot.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionDto {
    private String questionId;
    private String name;
    private GroupDto parent;
    private String title;
    private String text;
}
