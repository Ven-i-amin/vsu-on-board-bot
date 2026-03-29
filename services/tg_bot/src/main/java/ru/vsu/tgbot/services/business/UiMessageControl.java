package ru.vsu.tgbot.services.business;

import org.apache.commons.lang3.tuple.Pair;
import ru.vsu.tgbot.model.dto.UiMessageDto;
import ru.vsu.tgbot.util.UiMessageName;

public interface UiMessageControl {
    UiMessageDto getUiMessage(String name);
    UiMessageDto getUiMessage(UiMessageName name);

    String getUiMessageText(UiMessageName name, String langCode);
    Pair<String, String> getUiMessageNameAndText(UiMessageName name, String langCode);
}
