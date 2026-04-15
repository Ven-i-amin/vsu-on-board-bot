package ru.vsu.apigateway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Mono;
import ru.vsu.apigateway.config.telegram.TgBotForwardProperties;

@Service
@RequiredArgsConstructor
public class TgBotUpdateForwarder {
    private final WebClient webClient;
    private final TgBotForwardProperties properties;

    public Mono<Void> forward(Update update) {
        WebClient.RequestBodySpec request = webClient.post()
            .uri(properties.getUpdateUri())
            .contentType(MediaType.APPLICATION_JSON);

        if (StringUtils.hasText(properties.getInternalToken())) {
            request.header("X-Internal-Bot-Token", properties.getInternalToken());
        }

        return request
            .bodyValue(update)
            .retrieve()
            .toBodilessEntity()
            .then();
    }
}
