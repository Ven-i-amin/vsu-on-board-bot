package ru.vsu.core.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import ru.vsu.core.component.mapper.QuestionMapper;
import ru.vsu.core.model.dto.QuestionDto;
import ru.vsu.core.model.entity.Question;
import ru.vsu.core.model.request.QuestionCreateRequest;
import ru.vsu.core.model.request.QuestionUpdateRequest;
import ru.vsu.core.model.response.TopQuestionResponse;
import ru.vsu.core.repository.QuestionRepository;
import ru.vsu.core.service.QuestionService;
import ru.vsu.core.util.LocalizationUtil;
import ru.vsu.core.util.TransliterationUtil;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;
    private final MongoTemplate mongoTemplate;

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
    public QuestionDto save(QuestionCreateRequest question) {
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
        return saveWithUniqueName(newQuestion);
    }

    @Override
    public void updateGroupName(String oldGroupName, String newGroupName) {
        Query query = new Query(Criteria.where("groupName").is(oldGroupName));
        Update update = new Update().set("groupName", newGroupName);

        mongoTemplate.updateMulti(query, update, Question.class);
    }

    @Override
    public QuestionDto updateTitleAndText(String questionKey, QuestionUpdateRequest question) {
        if (!hasDefaultLanguage(question.title(), question.text())) {
            throw new IllegalArgumentException("Question must have a default language");
        }

        Question oldQuestion = findByIdOrName(questionKey);

        String oldName = LocalizationUtil.localize(oldQuestion.getTitle(), LocalizationUtil.DEFAULT_LANGUAGE_CODE);
        String newName = LocalizationUtil.localize(question.title(), LocalizationUtil.DEFAULT_LANGUAGE_CODE);

        oldQuestion.setTitle(question.title());
        oldQuestion.setText(question.text());

        if (!oldName.equals(newName)) {
            oldQuestion.setName(TransliterationUtil.transliterate(newName));
        }

        return saveWithUniqueName(oldQuestion);
    }

    @Override
    public void deleteById(String questionKey) {
        Question question = findByIdOrName(questionKey);
        questionRepository.deleteById(question.getQuestionId());
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
    public void deleteByGroupNames(Collection<String> groupNames) {
        questionRepository.deleteByGroupNameIn(groupNames);
    }

    @Override
    public void incrementUsing(String questionName) {
        Query query = new Query(Criteria.where("name").is(questionName));
        Update update = new  Update().inc("using", 1);

        mongoTemplate.updateMulti(query, update, Question.class);
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

    @Override
    public List<TopQuestionResponse> findTopQuestions() {
        return questionRepository.findAll().stream()
                .sorted((left, right) -> Integer.compare(
                        right.getUsing() == null ? 0 : right.getUsing(),
                        left.getUsing() == null ? 0 : left.getUsing()
                ))
                .map(questionMapper::toTopDto)
                .toList();
    }

    private QuestionDto saveWithUniqueName(Question question) {
        String baseName = question.getName();
        int suffix = 0;

        while (true) {
            try {
                return questionMapper.toDto(questionRepository.save(question));
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

    private Question findByIdOrName(String questionKey) {
        return questionRepository.findById(questionKey)
                .or(() -> questionRepository.findByName(questionKey))
                .orElseThrow(IllegalArgumentException::new);
    }
}
