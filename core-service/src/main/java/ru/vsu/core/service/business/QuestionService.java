package ru.vsu.core.service.business;

import ru.vsu.core.model.dto.QuestionDto;
import ru.vsu.core.model.request.QuestionCreateRequest;
import ru.vsu.core.model.request.QuestionFileRequest;
import ru.vsu.core.model.request.QuestionUpdateRequest;
import ru.vsu.core.model.response.TopQuestionResponse;

import java.util.Collection;
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

    List<String> findFileHashesByQuestionId(String questionId);

    void updateFileList(String questionId, List<String> files);

    void deleteById(String questionId);

    void deleteByName(String questionName);

    void deleteByGroupName(String groupName);

    void deleteByGroupNames(Collection<String> groupNames);

    void incrementUsing(String questionName);
}
