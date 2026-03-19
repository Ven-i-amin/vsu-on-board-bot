package ru.vsu.tgbot.services.core;

import ru.vsu.tgbot.model.response.LanguageResponseDto;

import java.util.List;

public interface LanguageService {
    List<LanguageResponseDto> getLanguages();
}
