package ru.vsu.core.service;

import ru.vsu.core.model.dto.QuestionDto;
import ru.vsu.core.model.request.QuestionCreateRequest;
import ru.vsu.core.model.request.QuestionUpdateRequest;
import ru.vsu.core.model.response.TopQuestionResponse;

import java.util.List;

public interface QuestionService {
    List<QuestionDto> findAll();
    QuestionDto findById(String questionId);

    List<QuestionDto> findByParentGroupName(String groupName);
    QuestionDto findByParentGroupNameAndName(String groupName, String name);

    List<TopQuestionResponse> findTopQuestions();

    QuestionDto save(QuestionDto question);
    QuestionDto save(QuestionCreateRequest question);
    void updateGroupName(String oldGroupName, String newGroupName);
    QuestionDto updateTitleAndText(String questionId, QuestionUpdateRequest question);

    void deleteById(String questionId);
    void deleteByName(String questionName);
    void deleteByGroupName(String groupName);

    void incrementUsing(String questionName);
}
