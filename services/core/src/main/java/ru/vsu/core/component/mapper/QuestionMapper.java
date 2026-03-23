package ru.vsu.core.component.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import ru.vsu.core.model.dto.QuestionDto;
import ru.vsu.core.model.dto.QuestionLocalizedDto;
import ru.vsu.core.model.dto.GroupTreeDto;
import ru.vsu.core.model.entity.Group;
import ru.vsu.core.model.entity.Question;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface QuestionMapper {
    QuestionMapper INSTANCE = Mappers.getMapper(QuestionMapper.class);

    @org.mapstruct.Mapping(source = "groupId", target = "parent")
    QuestionDto toDto(Question question);

    List<QuestionDto> toDtoList(List<Question> questions);

    @org.mapstruct.Mapping(source = "parent", target = "groupId")
    Question toEntity(QuestionDto questionDto);

    List<Question> toEntityList(List<QuestionDto> questionDtos);

    default QuestionLocalizedDto toLocalizedDto(
            Question question,
            @Context String languageCode,
            @Context Map<String, Group> groupsById
    ) {
        if (question == null) {
            return null;
        }
        return QuestionLocalizedDto.builder()
                .questionId(question.getQuestionId())
                .name(question.getName())
                .parent(toShallowLocalizedGroup(groupsById.get(question.getGroupId()), languageCode))
                .title(localize(question.getTitle(), languageCode))
                .text(localize(question.getText(), languageCode))
                .build();
    }

    default List<QuestionLocalizedDto> toLocalizedDtoList(
            List<Question> questions,
            @Context String languageCode,
            @Context Map<String, Group> groupsById
    ) {
        return questions == null ? null : questions.stream()
                .map(question -> toLocalizedDto(question, languageCode, groupsById))
                .toList();
    }

    @Named("toShallowLocalizedGroup")
    default GroupTreeDto toShallowLocalizedGroup(Group group, @Context String languageCode) {
        if (group == null) {
            return null;
        }
        return GroupTreeDto.builder()
                .groupId(group.getGroupId())
                .title(group.getTitle())
                .build();
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
