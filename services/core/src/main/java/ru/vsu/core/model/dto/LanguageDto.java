package ru.vsu.core.model.dto;

import java.util.Map;

public record LanguageDto(
        String code,
        Map<String, String> name
) {
}
