package ru.vsu.core.component.bootstrap;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.vsu.core.model.dto.LanguageDto;
import ru.vsu.core.service.LanguageService;

@Component
@Order(10)
@RequiredArgsConstructor
public class LanguageBootstrap implements ApplicationRunner {
    private static final String DEFAULT_LANGUAGE_CODE = "ru";
    private static final String DEFAULT_LANGUAGE_NAME = "Русский";

    private final LanguageService languageService;

    @Override
    public void run(ApplicationArguments args) {
        if (languageService.findByCode(DEFAULT_LANGUAGE_CODE) != null) {
            return;
        }

        languageService.save(new LanguageDto(DEFAULT_LANGUAGE_CODE, DEFAULT_LANGUAGE_NAME));
    }
}
