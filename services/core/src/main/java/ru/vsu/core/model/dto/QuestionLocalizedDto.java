package ru.vsu.core.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionLocalizedDto {
    private String questionId;
    private String name;
    private GroupLocalizedDto parent;
    private String title;
    private String text;
}
