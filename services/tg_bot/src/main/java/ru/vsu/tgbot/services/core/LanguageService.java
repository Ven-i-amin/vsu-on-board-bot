package ru.vsu.tgbot.services.core;

import ru.vsu.tgbot.model.dto.LanguageDto;

import java.util.List;

public interface LanguageService {
    List<LanguageDto> getLanguages();
}
