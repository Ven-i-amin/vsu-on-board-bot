package ru.vsu.core.model.dto;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record GroupWithQuestionsDto(
    String name,
    Map<String, String> title,
    String parentName,
    List<QuestionDto> questions,
    Long depth
) {}
