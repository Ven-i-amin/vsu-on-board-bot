package ru.vsu.core.service;

import ru.vsu.core.model.dto.QuestionDto;

import java.util.List;

public interface QuestionService {
    List<QuestionDto> findAll();
    QuestionDto findById(String questionId);
    QuestionDto save(QuestionDto question);
    void deleteById(String questionId);
    List<QuestionDto> findByParentGroupName(String groupName);
    QuestionDto findByParentGroupNameAndName(String groupName, String name);
}
