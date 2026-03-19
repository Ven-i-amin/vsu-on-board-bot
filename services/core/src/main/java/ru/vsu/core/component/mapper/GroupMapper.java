package ru.vsu.core.component.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import ru.vsu.core.model.dto.GroupDto;
import ru.vsu.core.model.dto.GroupLocalizedDto;
import ru.vsu.core.model.dto.QuestionLocalizedDto;
import ru.vsu.core.model.entity.Group;
import ru.vsu.core.model.entity.Question;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface GroupMapper {
    GroupDto toDto(Group group);

    Group toEntity(GroupDto groupDto);

    List<GroupDto> toDtoList(List<Group> groups);

    List<Group> toEntityList(List<GroupDto> groupDtos);

    default GroupLocalizedDto toLocalizedDto(
            Group group,
            @Context String languageCode,
            @Context Map<String, Group> groupsById,
            @Context Map<String, Question> questionsById
    ) {
        if (group == null) {
            return null;
        }
        return GroupLocalizedDto.builder()
                .groupId(group.getGroupId())
                .title(localize(group.getTitle(), languageCode))
                .parent(toShallowLocalizedDto(groupsById.get(group.getParentId()), languageCode))
                .innerGroups(toShallowLocalizedDtoList(resolveGroups(group.getInnerGroups(), groupsById), languageCode))
                .questions(toLocalizedQuestionShallowList(resolveQuestions(group.getQuestions(), questionsById), languageCode, groupsById))
                .build();
    }

    default List<GroupLocalizedDto> toLocalizedDtoList(
            List<Group> groups,
            @Context String languageCode,
            @Context Map<String, Group> groupsById,
            @Context Map<String, Question> questionsById
    ) {
        return groups == null ? null : groups.stream()
                .map(group -> toLocalizedDto(group, languageCode, groupsById, questionsById))
                .toList();
    }

    @Named("toShallowLocalizedGroup")
    default GroupLocalizedDto toShallowLocalizedDto(Group group, @Context String languageCode) {
        if (group == null) {
            return null;
        }
        return GroupLocalizedDto.builder()
                .groupId(group.getGroupId())
                .title(localize(group.getTitle(), languageCode))
                .build();
    }

    default List<GroupLocalizedDto> toShallowLocalizedDtoList(List<Group> groups, @Context String languageCode) {
        return groups == null ? null : groups.stream()
                .map(group -> toShallowLocalizedDto(group, languageCode))
                .toList();
    }

    private List<Group> resolveGroups(List<String> groupIds, Map<String, Group> groupsById) {
        if (groupIds == null) {
            return null;
        }
        return groupIds.stream()
                .map(groupsById::get)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<Question> resolveQuestions(List<String> questionIds, Map<String, Question> questionsById) {
        if (questionIds == null) {
            return null;
        }
        return questionIds.stream()
                .map(questionsById::get)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<QuestionLocalizedDto> toLocalizedQuestionShallowList(
            List<Question> questions,
            String languageCode,
            Map<String, Group> groupsById
    ) {
        if (questions == null) {
            return null;
        }
        return questions.stream()
                .map(question -> QuestionLocalizedDto.builder()
                        .questionId(question.getQuestionId())
                        .name(question.getName())
                        .parent(toShallowLocalizedDto(groupsById.get(question.getParent()), languageCode))
                        .title(localize(question.getTitle(), languageCode))
                        .text(localize(question.getText(), languageCode))
                        .build())
                .toList();
    }

    @Named("localize")
    default String localize(Map<String, String> values, @Context String languageCode) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        if (languageCode != null && values.containsKey(languageCode)) {
            return values.get(languageCode);
        }
        return values.getOrDefault("ru", values.values().iterator().next());
    }
}
