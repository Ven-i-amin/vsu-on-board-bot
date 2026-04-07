package ru.vsu.tgbot.services.core;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.vsu.contract.model.response.LanguageResponseDto;
import ru.vsu.tgbot.components.mapper.CoreResponseMapper;
import ru.vsu.tgbot.model.dto.LanguageDto;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class LanguageServiceImpl implements LanguageService {
    private WebClient coreClient;
    private CoreResponseMapper coreResponseMapper;

    @Override
    public List<LanguageDto> getLanguages() {
        try {
            List<LanguageDto> languages = coreClient.get()
                    .uri("/languages")
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
