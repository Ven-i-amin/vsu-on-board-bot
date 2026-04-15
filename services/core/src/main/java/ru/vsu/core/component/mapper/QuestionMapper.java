package ru.vsu.core.component.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import ru.vsu.core.model.dto.GroupResponse;
import ru.vsu.core.model.dto.QuestionDto;
import ru.vsu.core.model.dto.QuestionLocalizedDto;
import ru.vsu.core.model.entity.Group;
import ru.vsu.core.model.entity.Question;
import ru.vsu.core.model.request.QuestionCreateRequest;
import ru.vsu.core.model.response.TopQuestionResponse;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface QuestionMapper {
    QuestionMapper INSTANCE = Mappers.getMapper(QuestionMapper.class);

    @org.mapstruct.Mapping(source = "groupName", target = "parent")
    QuestionDto toDto(Question question);

    TopQuestionResponse toTopDto(Question question);
    List<TopQuestionResponse> toTopDto(List<Question> questions);

    List<QuestionDto> toDtoList(List<Question> questions);

    @org.mapstruct.Mapping(source = "parent", target = "groupName")
    Question toEntity(QuestionDto questionDto);

    Question toEntity(QuestionCreateRequest question);

    List<Question> toEntityList(List<QuestionDto> questionDtos);

    default QuestionLocalizedDto toLocalizedDto(
            Question question,
            @Context String languageCode,
            @Context Map<String, Group> groupsByName
    ) {
        if (question == null) {
            return null;
        }
        return QuestionLocalizedDto.builder()
                .questionId(question.getQuestionId())
                .name(question.getName())
                .parent(toShallowLocalizedGroup(groupsByName.get(question.getGroupName()), languageCode))
                .title(localize(question.getTitle(), languageCode))
                .text(localize(question.getText(), languageCode))
                .build();
    }

    default List<QuestionLocalizedDto> toLocalizedDtoList(
            List<Question> questions,
            @Context String languageCode,
            @Context Map<String, Group> groupsByName
    ) {
        return questions == null ? null : questions.stream()
                .map(question -> toLocalizedDto(question, languageCode, groupsByName))
                .toList();
    }

    @Named("toShallowLocalizedGroup")
    default GroupResponse toShallowLocalizedGroup(Group group, @Context String languageCode) {
        if (group == null) {
            return null;
        }
        return GroupResponse.builder()
                .name(group.getName())
                .title(group.getTitle())
                .parentName(group.getPath() == null || group.getPath().isEmpty()
                        ? null
                        : group.getPath().get(group.getPath().size() - 1))
                .childrenNames(List.of())
                .questions(List.of())
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
