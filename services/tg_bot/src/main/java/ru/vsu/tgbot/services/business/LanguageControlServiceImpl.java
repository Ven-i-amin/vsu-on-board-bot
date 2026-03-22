package ru.vsu.tgbot.services.business;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.tgbot.model.dto.LanguageDto;
import ru.vsu.tgbot.services.core.LanguageService;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class LanguageControlServiceImpl implements LanguageControlService{
    private final LanguageService languageService;
    private final List<LanguageDto> languageList = new ArrayList<>();

    @PostConstruct
    private void init() {
        languageList.addAll(languageService.getLanguages());
    }


    @Override
    public LanguageDto getLanguage(String langCode) {
        return languageList.stream()
                .filter(lang -> lang.code().equals(langCode))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<LanguageDto> getLanguages() {
        return languageList.stream().toList();
    }
}
