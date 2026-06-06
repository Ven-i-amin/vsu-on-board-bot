package ru.vsu.core.service.business;

import ru.vsu.core.model.dto.LanguageDto;

import java.util.List;

public interface LanguageService {
    List<LanguageDto> findAll();
    LanguageDto findByCode(String code);
    LanguageDto save(LanguageDto language);
    void deleteByCode(String code);
}
