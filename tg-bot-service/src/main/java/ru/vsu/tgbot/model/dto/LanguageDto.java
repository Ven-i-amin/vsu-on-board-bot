package ru.vsu.tgbot.model.dto;

import java.util.Map;

public record LanguageDto(
        String code,
        Map<String, String> name
) {
}
