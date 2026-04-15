package ru.vsu.tgbot.services.core;

import org.springframework.http.MediaType;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vsu.contract.model.response.LanguageResponseDto;
import ru.vsu.tgbot.components.mapper.CoreResponseMapper;
import ru.vsu.tgbot.model.dto.LanguageDto;

import java.util.List;
import java.util.Map;

@Service
public class LanguageClientImpl implements LanguageClient {
    private static final Logger log = LoggerFactory.getLogger(LanguageClientImpl.class);
    private WebClient coreClient;
    private CoreResponseMapper coreResponseMapper;

    public LanguageClientImpl(WebClient coreClient, CoreResponseMapper coreResponseMapper) {
        this.coreClient = coreClient;
        this.coreResponseMapper = coreResponseMapper;
    }

    @Override
    public List<LanguageDto> getLanguages() {
        try {
            List<LanguageDto> languages = coreClient.get()
                    .uri("/bot/languages")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<LanguageResponseDto>>() {})
                    .map(coreResponseMapper::toLanguageDtoList)
                    .block();
            return languages == null || languages.isEmpty() ? fallbackLanguages() : languages;
        } catch (RuntimeException ex) {
            log.warn("Failed to load languages from core", ex);
            return fallbackLanguages();
        }
    }

    private List<LanguageDto> fallbackLanguages() {
        return List.of(new LanguageDto("ru", Map.of("ru", "Русский")));
    }
}
