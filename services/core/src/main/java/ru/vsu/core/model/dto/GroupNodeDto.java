package ru.vsu.core.model.dto;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record GroupNodeDto (
    String groupId,
    String name,
    Map<String, String> title,
    String parentId,
    List<QuestionDto> questions,
    int level
) {}
