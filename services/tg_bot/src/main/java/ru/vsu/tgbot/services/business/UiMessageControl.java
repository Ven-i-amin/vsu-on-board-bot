package ru.vsu.tgbot.services.business;

import org.apache.commons.lang3.tuple.Pair;
import ru.vsu.tgbot.model.dto.UiMessageDto;
import ru.vsu.tgbot.util.UiMessage;

public interface UiMessageControl {
    UiMessageDto getUiMessage(String name);
    UiMessageDto getUiMessage(UiMessage name);

    String getUiMessageText(UiMessage name, String langCode);
    Pair<String, String> getUiMessageNameAndText(UiMessage name, String langCode);
}
