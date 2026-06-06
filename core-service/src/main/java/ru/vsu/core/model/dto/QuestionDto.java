package ru.vsu.core.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class QuestionDto {
    private String questionId;
    private String name;
    private String parent;
    private Map<String, String> title;
    private Map<String, String> text;
    @Builder.Default
    private List<String> fileHashes = new ArrayList<>();
}
