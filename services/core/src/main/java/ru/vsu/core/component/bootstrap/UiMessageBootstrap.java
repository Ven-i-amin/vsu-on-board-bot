package ru.vsu.core.component.bootstrap;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.vsu.core.model.dto.UiMessageDto;
import ru.vsu.core.service.UiMessageService;

import java.util.List;
import java.util.Map;

@Component
@Order(30)
@RequiredArgsConstructor
public class UiMessageBootstrap implements ApplicationRunner {
    private static final String DEFAULT_LANGUAGE_CODE = "ru";
    private static final String ENGLISH_LANGUAGE_CODE = "en";

    private final UiMessageService uiMessageService;

    @Override
    public void run(ApplicationArguments args) {
        defaultMessages().forEach(this::ensureExists);
    }

    private void ensureExists(DefaultUiMessage message) {
        uiMessageService.save(UiMessageDto.builder()
                .name(message.name())
                .text(message.text())
                .build());
    }

    private List<DefaultUiMessage> defaultMessages() {
        return List.of(
                new DefaultUiMessage("back", localizedText("Назад", "Back")),
                new DefaultUiMessage("start", localizedText("В начало", "Home")),
                new DefaultUiMessage("welcome", localizedText("Добро пожаловать!", "Welcome!")),
                new DefaultUiMessage("main-menu", localizedText("Главное меню", "Main menu")),
                new DefaultUiMessage("language_title", localizedText("Выбрать язык", "Choose a language")),
                new DefaultUiMessage("question_listen", localizedText("Выберите раздел в главном меню.", "Choose a section in the main menu.")),
                new DefaultUiMessage("question_answer", localizedText("Выберите язык.", "Choose a language."))
        );
    }

    private Map<String, String> localizedText(String russianText, String englishText) {
        return Map.of(
                DEFAULT_LANGUAGE_CODE, russianText,
                ENGLISH_LANGUAGE_CODE, englishText
        );
    }

    private record DefaultUiMessage(String name, Map<String, String> text) {
    }
}
