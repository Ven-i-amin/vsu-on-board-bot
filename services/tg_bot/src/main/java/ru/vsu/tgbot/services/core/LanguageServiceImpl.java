package ru.vsu.tgbot.services.core;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.vsu.tgbot.components.mapper.CoreResponseMapper;
import ru.vsu.tgbot.model.dto.LanguageDto;
import ru.vsu.tgbot.model.response.LanguageResponseDto;

import java.util.List;

@Service
@AllArgsConstructor
public class LanguageServiceImpl implements LanguageService {
    private WebClient coreClient;
    private CoreResponseMapper coreResponseMapper;

    @Override
    public List<LanguageDto> getLanguages() {
        return coreClient.get()
                .uri("/languages")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<LanguageResponseDto>>() {})
                .map(coreResponseMapper::toLanguageDtoList)
                .block();
    }
}
