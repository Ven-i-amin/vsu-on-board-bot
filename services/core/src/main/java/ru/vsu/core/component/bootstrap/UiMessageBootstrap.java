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

    private final UiMessageService uiMessageService;

    @Override
    public void run(ApplicationArguments args) {
        defaultMessages().forEach(this::ensureExists);
    }

    private void ensureExists(DefaultUiMessage message) {
        if (uiMessageService.findByName(message.name()) != null) {
            return;
        }

        uiMessageService.save(UiMessageDto.builder()
                .name(message.name())
                .text(Map.of(DEFAULT_LANGUAGE_CODE, message.text()))
                .build());
    }

    private List<DefaultUiMessage> defaultMessages() {
        return List.of(
                new DefaultUiMessage("back", "Назад"),
                new DefaultUiMessage("start", "В начало"),
                new DefaultUiMessage("welcome", "Добро пожаловать!"),
                new DefaultUiMessage("main-menu", "Главное меню"),
                new DefaultUiMessage("language_title", "Выбрать язык"),
                new DefaultUiMessage("question_listen", "Выберите раздел в главном меню."),
                new DefaultUiMessage("question_answer", "Выберите язык.")
        );
    }

    private record DefaultUiMessage(String name, String text) {
    }
}
