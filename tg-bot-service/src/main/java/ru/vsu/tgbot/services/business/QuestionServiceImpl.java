package ru.vsu.tgbot.services.business;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.tgbot.services.core.QuestionClient;

@Service
@AllArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private final QuestionClient questionClient;

    @Override
    public void fixate(String questionName) {
        questionClient.recordQuestionClicked(questionName);
    }
}
