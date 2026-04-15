package ru.vsu.core.component.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.vsu.contract.model.response.GroupResponseDto;
import ru.vsu.contract.model.response.LanguageResponseDto;
import ru.vsu.contract.model.response.UiMessageResponseDto;
import ru.vsu.contract.model.response.UserResponseDto;
import ru.vsu.core.model.dto.*;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface ResponseMapper {
    ResponseMapper INSTANCE = Mappers.getMapper(ResponseMapper.class);

    @Mapping(target = "innerGroups", expression = "java(toInnerGroups(group.childrenNames(), group.name()))")
    @Mapping(target = "questions", expression = "java(group.questions() == null ? java.util.List.of() : group.questions().stream().map(this::toResponse).toList())")
    GroupResponseDto toResponse(GroupResponse group);

    List<GroupResponseDto> toResponse(List<GroupResponse> groups);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "innerGroups", expression = "java(java.util.List.of())")
    @Mapping(target = "questions", ignore = true)
    GroupResponseDto toShallowResponse(GroupDto group);

    ru.vsu.contract.model.response.QuestionResponseDto toResponse(QuestionDto question);

    LanguageResponseDto toResponse(LanguageDto language);

    UserResponseDto toResponse(UserDto user);

    UiMessageResponseDto toResponse(UiMessageDto uiMessage);

    default List<GroupResponseDto> toInnerGroups(List<String> childNames, String parentName) {
        if (childNames == null || childNames.isEmpty()) {
            return List.of();
        }

        return childNames.stream()
                .map(childName -> new GroupResponseDto(childName, Map.of(), parentName, List.of(), List.of()))
                .toList();
    }
}
