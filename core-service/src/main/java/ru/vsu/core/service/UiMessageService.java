package ru.vsu.core.service;

import ru.vsu.contract.model.response.UiMessageResponseDto;
import ru.vsu.core.model.dto.UiMessageDto;
import ru.vsu.core.model.request.UiMessageUpdateRequest;

import java.util.List;

public interface UiMessageService {
    List<UiMessageDto> findAll();
    boolean existsByName(String name);
    UiMessageDto findByName(String name);
    UiMessageDto save(UiMessageDto uiMessage);

    UiMessageDto update(String name, UiMessageUpdateRequest uiMessage);

    void deleteByName(String name);
}
