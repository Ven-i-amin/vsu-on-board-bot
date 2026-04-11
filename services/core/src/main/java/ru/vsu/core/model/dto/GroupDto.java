package ru.vsu.core.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record GroupDto(
        String groupId,
        String name,
        Map<String, String> title,
        String parentName,
        @JsonIgnore
        List<String> path,
        Long depthLevel
) {
}
