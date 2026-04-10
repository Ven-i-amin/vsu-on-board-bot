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
        if (uiMessageService.existsByName(message.name())) {
            return;
        }

        uiMessageService.save(UiMessageDto.builder()
                .name(message.name())
                .text(message.text())
                .build());
    }

    private List<DefaultUiMessage> defaultMessages() {
        return List.of(
                new DefaultUiMessage("back", localizedText("\u041d\u0430\u0437\u0430\u0434", "Back")),
                new DefaultUiMessage("start", localizedText("\u0412 \u043d\u0430\u0447\u0430\u043b\u043e", "Home")),
                new DefaultUiMessage("welcome", localizedText("\u0414\u043e\u0431\u0440\u043e \u043f\u043e\u0436\u0430\u043b\u043e\u0432\u0430\u0442\u044c!", "Welcome!")),
                new DefaultUiMessage("main-menu", localizedText("\u0413\u043b\u0430\u0432\u043d\u043e\u0435 \u043c\u0435\u043d\u044e", "Main menu")),
                new DefaultUiMessage("language-title", localizedText("\u0412\u044b\u0431\u0440\u0430\u0442\u044c \u044f\u0437\u044b\u043a", "Choose a language")),
                new DefaultUiMessage("question-listen", localizedText("\u0412\u044b\u0431\u0435\u0440\u0438\u0442\u0435 \u0440\u0430\u0437\u0434\u0435\u043b \u0432 \u0433\u043b\u0430\u0432\u043d\u043e\u043c \u043c\u0435\u043d\u044e.", "Choose a section in the main menu.")),
                new DefaultUiMessage("question-answer", localizedText("\u0412\u044b\u0431\u0435\u0440\u0438\u0442\u0435 \u044f\u0437\u044b\u043a.", "Choose a language."))
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
