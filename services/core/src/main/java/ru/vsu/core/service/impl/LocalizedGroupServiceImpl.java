package ru.vsu.core.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.core.model.dto.GroupDto;
import ru.vsu.core.model.dto.GroupLocalizedDto;
import ru.vsu.core.model.dto.LanguageDto;
import ru.vsu.core.model.dto.QuestionDto;
import ru.vsu.core.model.dto.QuestionLocalizedDto;
import ru.vsu.core.service.GroupService;
import ru.vsu.core.service.LanguageService;
import ru.vsu.core.service.LocalizedGroupService;
import ru.vsu.core.service.QuestionService;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class LocalizedGroupServiceImpl implements LocalizedGroupService {
    public static final String DEFAULT_LANGUAGE_CODE = "ru";

    private final GroupService groupService;
    private final QuestionService questionService;
    private final LanguageService languageService;

    @Override
    public GroupLocalizedDto findById(String groupId, String languageCode) {
        return findById(groupId, languageCode, 0);
    }

    @Override
    public GroupLocalizedDto findById(String groupId, String languageCode, int depth) {
        int normalizedDepth = Math.max(depth, 0);
        String resolvedLanguageCode = resolveLanguageCode(languageCode);
        GroupDto group = groupService.findById(groupId);
        return group == null ? null : localizeGroup(group, resolvedLanguageCode, normalizedDepth, new HashSet<>());
    }

    @Override
    public List<GroupLocalizedDto> findInnerByParentId(String parentId, String languageCode) {
        String resolvedLanguageCode = resolveLanguageCode(languageCode);
        return groupService.findByParentId(parentId).stream()
                .map(group -> localizeGroup(group, resolvedLanguageCode, 0, new HashSet<>()))
                .toList();
    }

    @Override
    public List<GroupLocalizedDto> findByParentIds(List<String> parentIds, String languageCode) {
        String resolvedLanguageCode = resolveLanguageCode(languageCode);
        return groupService.findByParentIds(parentIds).stream()
                .map(group -> localizeGroup(group, resolvedLanguageCode, 0, new HashSet<>()))
                .toList();
    }

    @Override
    public GroupLocalizedDto findStartGroup(String languageCode) {
        return findStartGroup(languageCode, 3);
    }

    @Override
    public GroupLocalizedDto findStartGroup(String languageCode, int depth) {
        int normalizedDepth = Math.max(depth, 0);
        String resolvedLanguageCode = resolveLanguageCode(languageCode);
        return groupService.findAll().stream()
                .filter(group -> group.parentId() == null || group.parentId().isBlank())
                .findFirst()
                .map(group -> localizeGroup(group, resolvedLanguageCode, normalizedDepth, new HashSet<>()))
                .orElse(null);
    }

    private GroupLocalizedDto localizeGroup(GroupDto group, String languageCode, int depth, Set<String> visitedGroupIds) {
        if (group == null) {
            return null;
        }
        if (!visitedGroupIds.add(group.groupId())) {
            return toShallowLocalizedGroup(group, languageCode);
        }

        GroupLocalizedDto localizedGroup = GroupLocalizedDto.builder()
                .groupId(group.groupId())
                .title(localize(group.title(), languageCode))
                .parent(resolveParent(group.parentId(), languageCode))
                .innerGroups(depth > 0
                        ? resolveInnerGroups(group.innerGroups(), languageCode, depth - 1, visitedGroupIds)
                        : Collections.emptyList())
                .questions(resolveQuestions(group.questions(), languageCode))
                .build();

        visitedGroupIds.remove(group.groupId());
        return localizedGroup;
    }

    private GroupLocalizedDto resolveParent(String parentId, String languageCode) {
        if (parentId == null) {
            return null;
        }
        GroupDto group = groupService.findById(parentId);
        return group == null ? null : toShallowLocalizedGroup(group, languageCode);
    }

    private List<GroupLocalizedDto> resolveInnerGroups(
            List<String> innerGroupIds,
            String languageCode,
            int depth,
            Set<String> visitedGroupIds
    ) {
        if (innerGroupIds == null || innerGroupIds.isEmpty()) {
            return Collections.emptyList();
        }
        return innerGroupIds.stream()
                .map(groupService::findById)
                .filter(java.util.Objects::nonNull)
                .map(group -> localizeGroup(group, languageCode, depth, visitedGroupIds))
                .toList();
    }

    private List<QuestionLocalizedDto> resolveQuestions(List<String> questionIds, String languageCode) {
        if (questionIds == null || questionIds.isEmpty()) {
            return Collections.emptyList();
        }
        return questionIds.stream()
                .map(questionService::findById)
                .filter(java.util.Objects::nonNull)
                .map(question -> localizeQuestion(question, languageCode))
                .toList();
    }

    private QuestionLocalizedDto localizeQuestion(QuestionDto question, String languageCode) {
        return QuestionLocalizedDto.builder()
                .questionId(question.getQuestionId())
                .name(question.getName())
                .parent(resolveParent(question.getParent(), languageCode))
                .title(localize(question.getTitle(), languageCode))
                .text(localize(question.getText(), languageCode))
                .build();
    }

    private GroupLocalizedDto toShallowLocalizedGroup(GroupDto group, String languageCode) {
        return GroupLocalizedDto.builder()
                .groupId(group.groupId())
                .title(localize(group.title(), languageCode))
                .parent(null)
                .innerGroups(Collections.emptyList())
                .questions(Collections.emptyList())
                .build();
    }

    private String resolveLanguageCode(String languageCode) {
        if (languageCode != null && languageService.findByCode(languageCode) != null) {
            return languageCode;
        }
        LanguageDto defaultLanguage = languageService.findByCode(DEFAULT_LANGUAGE_CODE);
        if (defaultLanguage != null) {
            return defaultLanguage.code();
        }
        return languageService.findAll().stream()
                .map(LanguageDto::code)
                .findFirst()
                .orElse(languageCode);
    }

    private String localize(java.util.Map<String, String> values, String languageCode) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        if (languageCode != null && values.containsKey(languageCode)) {
            return values.get(languageCode);
        }
        return values.getOrDefault(DEFAULT_LANGUAGE_CODE, values.values().iterator().next());
    }
}
