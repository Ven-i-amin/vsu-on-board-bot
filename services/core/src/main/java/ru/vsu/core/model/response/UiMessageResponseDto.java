package ru.vsu.core.model.response;

import java.util.Map;

public record UiMessageResponseDto(
        String id,
        String name,
        Map<String, String> text
) {
}
