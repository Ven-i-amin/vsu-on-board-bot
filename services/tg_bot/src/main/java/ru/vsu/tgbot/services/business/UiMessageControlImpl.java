package ru.vsu.tgbot.services.business;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.tgbot.model.dto.LanguageDto;
import ru.vsu.tgbot.model.dto.UiMessageDto;
import ru.vsu.tgbot.services.core.UiMessageService;
import ru.vsu.tgbot.util.MessageUtil;
import ru.vsu.tgbot.util.UiMessage;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UiMessageControlImpl implements UiMessageControl {
    private final UiMessageService uiMessageService;
    private final LanguageControl languageControl;

    private List<UiMessageDto> uiMessageList;

    @PostConstruct
    public void init() {
        uiMessageList = uiMessageService.getUiMessages();
    }

    @Override
    public UiMessageDto getUiMessage(String name) {
        return uiMessageList.stream()
                .filter(ui -> ui.getName().equals(name))
                .findFirst()
                .orElse(getErrorMessage(name));
    }

    @Override
    public UiMessageDto getUiMessage(UiMessage name) {
        return uiMessageList.stream()
                .filter(ui -> ui.getName().equals(name.getValue()))
                .findFirst()
                .orElse(getErrorMessage(name.getValue()));
    }

    @Override
    public String getUiMessageText(UiMessage name, String langCode) {
        UiMessageDto uiMessage = uiMessageList.stream()
                .filter(ui -> ui.getName().equals(name.getValue()))
                .findFirst()
                .orElse(null);

        if (uiMessage == null) {
            return MessageUtil.NOT_FOUND_MESSAGE;
        }

        return uiMessage.getText().getOrDefault(langCode, MessageUtil.NOT_FOUND_MESSAGE);
    }

    private UiMessageDto getErrorMessage(String name) {
        return UiMessageDto.builder()
                .name(name)
                .text(languageControl.getLanguages().stream()
                        .map(LanguageDto::code)
                        .collect(Collectors.toMap(
                                Function.identity(),
                                lang -> MessageUtil.NOT_FOUND_MESSAGE)
                        )
                )
                .build();
    }
}
