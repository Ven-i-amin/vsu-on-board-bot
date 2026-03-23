package ru.vsu.core.service;

import ru.vsu.core.model.dto.UiMessageDto;

import java.util.List;

public interface UiMessageService {
    List<UiMessageDto> findAll();
    UiMessageDto findByName(String name);
    UiMessageDto save(UiMessageDto uiMessage);
    void deleteByName(String name);
}
