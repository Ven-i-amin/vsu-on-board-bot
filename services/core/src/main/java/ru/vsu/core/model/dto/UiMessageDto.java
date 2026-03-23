package ru.vsu.core.model.dto;

import lombok.Builder;

import java.util.Map;

@Builder
public record UiMessageDto(
        String id,
        String name,
        Map<String, String> text
) {
}
