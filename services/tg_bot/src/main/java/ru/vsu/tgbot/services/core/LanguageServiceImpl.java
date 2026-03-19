package ru.vsu.tgbot.services.core;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.vsu.tgbot.model.response.LanguageResponseDto;

import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class LanguageServiceImpl implements LanguageService {
    private WebClient coreClient;

    @Override
    public List<LanguageResponseDto> getLanguages() {
        return coreClient.get()
                .uri("/languages")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(LanguageResponseDto.class)
                .map(Arrays::asList)
                .block();
    }
}
