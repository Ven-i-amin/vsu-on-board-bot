package ru.vsu.tgbot.services.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.vsu.tgbot.model.dto.QuestionFileDto;

import java.util.List;

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

    @Override
    public List<QuestionFileDto> getQuestionFiles(String questionId) {
        try {
            List<QuestionFileDto> files = coreClient.get()
                    .uri("/question/{questionId}/files", questionId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<QuestionFileDto>>() {})
                    .block();
            return files != null ? files : List.of();
        } catch (RuntimeException ex) {
            log.warn("Failed to fetch files for question '{}' from core", questionId, ex);
            return List.of();
        }
    }

    @Override
    public byte[] getFileContent(String fileHash) {
        return coreClient.get()
                .uri("/file/{fileHash}/content", fileHash)
                .retrieve()
                .bodyToMono(byte[].class)
                .block();
    }
}
