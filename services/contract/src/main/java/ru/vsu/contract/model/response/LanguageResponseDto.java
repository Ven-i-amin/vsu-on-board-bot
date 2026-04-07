package ru.vsu.contract.model.response;

import java.util.Map;

public record LanguageResponseDto(
        String code,
        Map<String, String> name
) {
}
