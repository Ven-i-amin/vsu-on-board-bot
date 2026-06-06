package ru.vsu.tgbot.services.core;

import ru.vsu.tgbot.model.dto.QuestionFileDto;

import java.util.List;

public interface QuestionClient {
    void recordQuestionClicked(String questionName);
    List<QuestionFileDto> getQuestionFiles(String questionId);
    byte[] getFileContent(String fileHash);
}
