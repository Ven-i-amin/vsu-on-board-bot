package ru.vsu.tgbot.services.business;

import ru.vsu.tgbot.model.dto.LanguageDto;

import java.util.List;

public interface LanguageControl {
    LanguageDto getLanguage(String langCode);
    List<LanguageDto> getLanguages();
}
