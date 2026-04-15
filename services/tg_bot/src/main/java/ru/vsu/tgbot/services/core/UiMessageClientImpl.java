package ru.vsu.tgbot.services.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.vsu.contract.model.response.UiMessageResponseDto;
import ru.vsu.tgbot.components.mapper.CoreResponseMapper;
import ru.vsu.tgbot.model.dto.UiMessageDto;

import java.util.List;
import java.util.Map;

@Service
public class UiMessageClientImpl implements UiMessageClient {
    private static final Logger log = LoggerFactory.getLogger(UiMessageClientImpl.class);
    private final WebClient coreClient;
    private final CoreResponseMapper coreResponseMapper;

    public UiMessageClientImpl(WebClient coreClient, CoreResponseMapper coreResponseMapper) {
        this.coreClient = coreClient;
        this.coreResponseMapper = coreResponseMapper;
    }

    @Override
    public List<UiMessageDto> getUiMessages() {
        try {
            List<UiMessageDto> messages = coreClient.get()
                    .uri("/bot/uiMessages")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<UiMessageResponseDto>>() {})
                    .map(coreResponseMapper::toUiMessageDtoList)
                    .block();
            return messages == null || messages.isEmpty() ? fallbackMessages() : messages;
        } catch (RuntimeException ex) {
            log.warn("Failed to load UI messages from core", ex);
            return fallbackMessages();
        }
    }

    private List<UiMessageDto> fallbackMessages() {
        return List.of(
                stub("back", "Назад"),
                stub("start", "В начало"),
                stub("welcome", "Добро пожаловать!"),
                stub("main-menu", "Главное меню"),
                stub("language-title", "Выбрать язык"),
                stub("question-listen", "Выберите раздел в главном меню."),
                stub("question-answer", "Выберите язык.")
        );
    }

    private UiMessageDto stub(String name, String text) {
        return UiMessageDto.builder()
                .name(name)
                .text(Map.of("ru", text))
                .build();
    }
}
