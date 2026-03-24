package ru.vsu.tgbot.components.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.vsu.contract.model.response.GroupResponseDto;
import ru.vsu.contract.model.response.LanguageResponseDto;
import ru.vsu.contract.model.response.QuestionResponseDto;
import ru.vsu.contract.model.response.UiMessageResponseDto;
import ru.vsu.contract.model.response.UserResponseDto;
import ru.vsu.tgbot.model.dto.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CoreResponseMapper {
    @Mapping(target = "innerGroups", defaultExpression = "java( new java.util.ArrayList<>() )")
    GroupDto toGroupDto(GroupResponseDto groupResponseDto);

    @Mapping(target = "innerGroups", defaultExpression = "java( new java.util.ArrayList<>() )")
    List<GroupDto> toGroupDtoList(List<GroupResponseDto> groupResponseDtos);

    QuestionDto toQuestionDto(QuestionResponseDto questionResponseDto);

    List<QuestionDto> toQuestionDtoList(List<QuestionResponseDto> questionResponseDtos);

    LanguageDto toLanguageDto(LanguageResponseDto languageResponseDto);

    List<LanguageDto> toLanguageDtoList(List<LanguageResponseDto> languageResponseDtos);

    @Mapping(source = "langCode", target = "langCode")
    UserDto toUserDto(UserResponseDto userResponseDto);

    @Mapping(source = "langCode", target = "langCode")
    UserResponseDto toUserResponseDto(UserDto userDto);

    UiMessageDto toUiMessageDto(UiMessageResponseDto uiMessageResponseDto);
    List<UiMessageDto> toUiMessageDtoList(List<UiMessageResponseDto> uiMessageResponseDtos);
}
