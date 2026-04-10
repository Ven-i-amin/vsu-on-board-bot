package ru.vsu.tgbot.services.core;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@AllArgsConstructor
public class UiMessageServiceImpl implements UiMessageService {
    private final WebClient coreClient;
    private final CoreResponseMapper coreResponseMapper;

    @Override
    public List<UiMessageDto> getUiMessages() {
        try {
            List<UiMessageDto> messages = coreClient.get()
                    .uri("/uiMessages")
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
                stub("back", "\u041d\u0430\u0437\u0430\u0434"),
                stub("start", "\u0412 \u043d\u0430\u0447\u0430\u043b\u043e"),
                stub("welcome", "\u0414\u043e\u0431\u0440\u043e \u043f\u043e\u0436\u0430\u043b\u043e\u0432\u0430\u0442\u044c!"),
                stub("main-menu", "\u0413\u043b\u0430\u0432\u043d\u043e\u0435 \u043c\u0435\u043d\u044e"),
                stub("language-title", "\u0412\u044b\u0431\u0440\u0430\u0442\u044c \u044f\u0437\u044b\u043a"),
                stub("question-listen", "\u0412\u044b\u0431\u0435\u0440\u0438\u0442\u0435 \u0440\u0430\u0437\u0434\u0435\u043b \u0432 \u0433\u043b\u0430\u0432\u043d\u043e\u043c \u043c\u0435\u043d\u044e."),
                stub("question-answer", "\u0412\u044b\u0431\u0435\u0440\u0438\u0442\u0435 \u044f\u0437\u044b\u043a.")
        );
    }

    private UiMessageDto stub(String name, String text) {
        return UiMessageDto.builder()
                .name(name)
                .text(Map.of("ru", text))
                .build();
    }
}
