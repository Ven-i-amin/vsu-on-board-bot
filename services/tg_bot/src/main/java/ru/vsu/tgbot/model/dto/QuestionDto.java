package ru.vsu.tgbot.model.dto;

import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class QuestionDto {
    private String questionId;
    private String name;
    private String parent;
    @NotNull
    private Map<String, String> title = new HashMap<>();
    @NotNull
    private Map<String, String> text = new HashMap<>();
}
