package ru.vsu.tgbot.model.response;

import org.jetbrains.annotations.NotNull;

public record LanguageResponseDto(
        @NotNull String code,
        @NotNull String name
) {
}
