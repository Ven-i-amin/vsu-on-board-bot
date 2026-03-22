package ru.vsu.tgbot.services.core;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.vsu.tgbot.components.mapper.CoreResponseMapper;
import ru.vsu.tgbot.model.dto.UiMessageDto;
import ru.vsu.tgbot.model.response.UiMessageResponse;

import java.util.List;

@Service
@AllArgsConstructor
public class UiMessageServiceImpl implements  UiMessageService {
    private WebClient coreClient;
    private CoreResponseMapper coreResponseMapper;

    @Override
    public List<UiMessageDto> getUiMessages() {
        return coreClient.get()
                .uri("/uiMessages")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(UiMessageResponse.class)
                .map(List::of)
                .map(el -> coreResponseMapper.toUiMessageDtoList(el))
                .block();
    }
}
