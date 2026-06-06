package ru.vsu.core.component.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.vsu.contract.model.response.GroupResponseDto;
import ru.vsu.contract.model.response.LanguageResponseDto;
import ru.vsu.contract.model.response.UiMessageResponseDto;
import ru.vsu.contract.model.response.UserResponseDto;
import ru.vsu.core.model.dto.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ResponseMapper {
    GroupResponseDto toResponse(GroupDto group);

    List<GroupResponseDto> toResponseList(List<GroupDto> groups);

    ru.vsu.contract.model.response.QuestionResponseDto toResponse(QuestionDto question);

    LanguageResponseDto toResponse(LanguageDto language);

    UserResponseDto toResponse(UserDto user);

    UiMessageResponseDto toResponse(UiMessageDto uiMessage);
}