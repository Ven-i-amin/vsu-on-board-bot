package ru.vsu.core.component.bootstrap;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.vsu.core.model.dto.LanguageDto;
import ru.vsu.core.service.LanguageService;

import java.util.List;
import java.util.Map;

@Component
@Order(10)
@RequiredArgsConstructor
public class LanguageBootstrap implements ApplicationRunner {
    private final LanguageService languageService;

    @Override
    public void run(ApplicationArguments args) {
        defaultLanguages().forEach(this::ensureExists);
    }

    private void ensureExists(LanguageDto language) {
        if (languageService.findByCode(language.code()) != null) {
            return;
        }

        languageService.save(language);
    }

    private List<LanguageDto> defaultLanguages() {
        return List.of(
                new LanguageDto(
                        "ru",
                        Map.of(
                                "ru", "Русский",
                                "en", "Russian",
                                "fr", "Russe",
                                "es", "Ruso",
                                "zh", "俄语"
                        )
                ),
                new LanguageDto(
                        "en",
                        Map.of(
                                "ru", "Английский",
                                "en", "English",
                                "fr", "Anglais",
                                "es", "Ingles",
                                "zh", "英语"
                        )
                ),
                new LanguageDto(
                        "fr",
                        Map.of(
                                "ru", "Французский",
                                "en", "French",
                                "fr", "Français",
                                "es", "Frances",
                                "zh", "法语"
                        )
                ),
                new LanguageDto(
                        "es",
                        Map.of(
                                "ru", "Испанский",
                                "en", "Spanish",
                                "fr", "Espagnol",
                                "es", "Español",
                                "zh", "西班牙语"
                        )
                ),
                new LanguageDto(
                        "zh",
                        Map.of(
                                "ru", "Китайский",
                                "en", "Chinese",
                                "fr", "Chinois",
                                "es", "Chino",
                                "zh", "中文"
                        )
                )
        );
    }
}
