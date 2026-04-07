package ru.vsu.core.service;

import ru.vsu.core.model.dto.QuestionDto;
import ru.vsu.core.model.request.QuestionCreateRequest;
import ru.vsu.core.model.request.QuestionUpdateRequest;

import java.util.List;

public interface QuestionService {
    List<QuestionDto> findAll();
    QuestionDto findById(String questionId);
    QuestionDto save(QuestionDto question);

    void save(QuestionCreateRequest question);

    void updateTitleAndText(String questionName, QuestionUpdateRequest question);

    void deleteById(String questionId);
    void deleteByName(String questionName);
    void deleteByGroupName(String groupName);
    List<QuestionDto> findByParentGroupName(String groupName);
    QuestionDto findByParentGroupNameAndName(String groupName, String name);
}
