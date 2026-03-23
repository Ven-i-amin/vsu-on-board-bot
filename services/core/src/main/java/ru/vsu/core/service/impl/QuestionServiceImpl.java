package ru.vsu.core.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.core.component.mapper.QuestionMapper;
import ru.vsu.core.model.dto.QuestionDto;
import ru.vsu.core.repository.QuestionRepository;
import ru.vsu.core.service.QuestionService;

import java.util.List;

@Service
@AllArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;

    @Override
    public List<QuestionDto> findAll() {
        return questionMapper.toDtoList(questionRepository.findAll());
    }

    @Override
    public QuestionDto findById(String questionId) {
        return questionRepository.findById(questionId)
                .map(questionMapper::toDto)
                .orElse(null);
    }

    @Override
    public QuestionDto save(QuestionDto question) {
        return questionMapper.toDto(questionRepository.save(questionMapper.toEntity(question)));
    }

    @Override
    public void deleteById(String questionId) {
        questionRepository.deleteById(questionId);
    }

    @Override
    public List<QuestionDto> findByParentGroupId(String groupId) {
        return questionMapper.toDtoList(questionRepository.findByGroupId(groupId));
    }

    @Override
    public QuestionDto findByParentGroupIdAndName(String groupId, String name) {
        return questionRepository.findByGroupIdAndName(groupId, name)
                .map(questionMapper::toDto)
                .orElse(null);
    }
}
