package ru.vsu.core.model.dto;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record GroupDto(
        String groupId,
        Map<String, String> title,
        String parentId,
        List<String> innerGroups,
        List<String> questions
) {
}
