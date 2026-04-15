package ru.vsu.tgbot.services.core;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@AllArgsConstructor
@Slf4j
public class QuestionClientImpl implements QuestionClient {
    private final WebClient coreClient;

    @Override
    public void recordQuestionClicked(String questionName) {
        coreClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/bot/question/{questionName}/fixate")
                        .build(questionName)
                )
                .retrieve()
                .toBodilessEntity()
                .subscribe();
    }
}
