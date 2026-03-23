package ru.vsu.tgbot.services.business;

import ru.vsu.tgbot.model.dto.UiMessageDto;
import ru.vsu.tgbot.util.UiMessage;

public interface UiMessageControl {
    UiMessageDto getUiMessage(String name);
    UiMessageDto getUiMessage(UiMessage name);

    String getUiMessageText(UiMessage name, String langCode);
}
