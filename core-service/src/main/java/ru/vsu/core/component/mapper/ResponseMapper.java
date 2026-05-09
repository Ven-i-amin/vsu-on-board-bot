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

@Mapper(componentModel = "spring")
public interface ResponseMapper {
    ResponseMapper INSTANCE = Mappers.getMapper(ResponseMapper.class);

    @Mapping(target = "innerGroups", expression = "java( toResponse(group.innerGroups()) )")
    @Mapping(target = "questions", expression = "java(group.questions() == null ? java.util.List.of() : group.questions().stream().map(this::toResponse).toList())")
    GroupResponseDto toResponse(GroupTreeDto group);

    List<GroupResponseDto> toResponse(List<GroupTreeDto> groups);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "innerGroups", ignore = true)
    @Mapping(target = "questions", ignore = true)
    GroupResponseDto toShallowResponse(GroupDto group);

    ru.vsu.contract.model.response.QuestionResponseDto toResponse(QuestionDto question);

    LanguageResponseDto toResponse(LanguageDto language);

    UserResponseDto toResponse(UserDto user);

    UiMessageResponseDto toResponse(UiMessageDto uiMessage);
}
