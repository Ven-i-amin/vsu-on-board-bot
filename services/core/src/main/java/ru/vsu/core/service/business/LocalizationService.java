package ru.vsu.core.service.business;

import ru.vsu.core.model.dto.LocalizationDto;

public interface LocalizationService {
    void saveGroupLocalization(String groupId, String title, String langCode);
    void saveQuestionLocalization(LocalizationDto localizationDto);
    void deleteGroupLocalization(String groupId, String code);
    void deleteQuestionLocalization(String questionId, String code);
}
