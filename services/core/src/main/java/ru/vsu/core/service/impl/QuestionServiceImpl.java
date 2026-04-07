package ru.vsu.core.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import ru.vsu.core.component.mapper.QuestionMapper;
import ru.vsu.core.model.dto.QuestionDto;
import ru.vsu.core.model.entity.Group;
import ru.vsu.core.model.entity.Question;
import ru.vsu.core.model.request.QuestionCreateRequest;
import ru.vsu.core.model.request.QuestionUpdateRequest;
import ru.vsu.core.repository.QuestionRepository;
import ru.vsu.core.service.QuestionService;
import ru.vsu.core.util.LocalizationUtil;
import ru.vsu.core.util.TransliterationUtil;

import java.util.List;
import java.util.Map;

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
    public void save(QuestionCreateRequest question) {
        if (question.groupName() == null) {
            throw new IllegalArgumentException("Question must have a group");
        }

        if (!hasDefaultLanguage(question.title(), question.text())) {
            throw new IllegalArgumentException("Question must have a default language");
        }

        Question newQuestion = questionMapper.toEntity(question);

        String newName = TransliterationUtil.transliterate(
                LocalizationUtil.localize(
                        question.title(),
                        LocalizationUtil.DEFAULT_LANGUAGE_CODE
                )
        );

        newQuestion.setName(newName);
        saveWithUniqueName(newQuestion);
    }

    @Override
    public void updateTitleAndText(String questionName, QuestionUpdateRequest question) {
        if (!hasDefaultLanguage(question.title(), question.text())) {
            throw new IllegalArgumentException("Question must have a default language");
        }

        Question oldQuestion = questionRepository.findByName(questionName).orElseThrow(IllegalArgumentException::new);

        String oldName = LocalizationUtil.localize(oldQuestion.getTitle(), LocalizationUtil.DEFAULT_LANGUAGE_CODE);
        String newName = LocalizationUtil.localize(question.title(), LocalizationUtil.DEFAULT_LANGUAGE_CODE);

        if (!oldName.equals(newName)) {
            oldQuestion.setName(TransliterationUtil.transliterate(newName));
        }

        saveWithUniqueName(oldQuestion);
    }

    @Override
    public void deleteById(String questionId) {
        questionRepository.deleteById(questionId);
    }

    @Override
    public void deleteByName(String questionName) {
        questionRepository.deleteByName(questionName);
    }

    @Override
    public void deleteByGroupName(String groupName) {
        questionRepository.deleteByGroupName(groupName);
    }

    @Override
    public List<QuestionDto> findByParentGroupName(String groupName) {
        return questionMapper.toDtoList(questionRepository.findByGroupName(groupName));
    }

    @Override
    public QuestionDto findByParentGroupNameAndName(String groupName, String name) {
        return questionRepository.findByGroupNameAndName(groupName, name)
                .map(questionMapper::toDto)
                .orElse(null);
    }

    private Question saveWithUniqueName(Question question) {
        String baseName = question.getName();
        int suffix = 0;

        while (true) {
            try {
                return questionRepository.save(question);
            } catch (DuplicateKeyException exception) {
                suffix++;
            }

            question.setName(suffix == 0 ? baseName : baseName + "-" + suffix);
        }
    }

    private boolean hasDefaultLanguage(Map<String, String> title, Map<String, String> text) {
        return LocalizationUtil.hasDefaultLanguage(title)
                && LocalizationUtil.hasDefaultLanguage(text);
    }
}
