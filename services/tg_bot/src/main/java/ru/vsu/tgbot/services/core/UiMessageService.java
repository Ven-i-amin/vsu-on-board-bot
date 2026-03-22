package ru.vsu.tgbot.services.core;

import ru.vsu.tgbot.model.dto.UiMessageDto;

import java.util.List;

public interface UiMessageService {
    List<UiMessageDto> getUiMessages();
}
