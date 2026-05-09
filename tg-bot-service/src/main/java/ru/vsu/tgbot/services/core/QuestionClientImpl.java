package ru.vsu.tgbot.services.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class QuestionClientImpl implements QuestionClient {
    private final WebClient coreClient;

    public QuestionClientImpl(@Qualifier("coreClient") WebClient coreClient) {
        this.coreClient = coreClient;
    }

    @Override
    public void recordQuestionClicked(String questionName) {
        coreClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/question/{questionName}/fixate")
                        .build(questionName)
                )
                .retrieve()
                .toBodilessEntity()
                .subscribe();
    }
}
