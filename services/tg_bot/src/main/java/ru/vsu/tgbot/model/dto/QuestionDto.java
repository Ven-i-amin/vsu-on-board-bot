package ru.vsu.tgbot.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class QuestionDto {
    private String questionId;
    private String name;
    private GroupDto parent;
    private Map<String, String> title;
    private Map<String, String> text;
}
