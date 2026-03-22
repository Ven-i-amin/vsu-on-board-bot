package ru.vsu.tgbot.components.mapper;

import org.mapstruct.Mapper;
import ru.vsu.tgbot.model.dto.*;
import ru.vsu.tgbot.model.response.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CoreResponseMapper {
    GroupDto toGroupDto(GroupResponseDto groupResponseDto);

    List<GroupDto> toGroupDtoList(List<GroupResponseDto> groupResponseDtos);

    QuestionDto toQuestionDto(QuestionResponseDto questionResponseDto);

    List<QuestionDto> toQuestionDtoList(List<QuestionResponseDto> questionResponseDtos);

    LanguageDto toLanguageDto(LanguageResponseDto languageResponseDto);

    List<LanguageDto> toLanguageDtoList(List<LanguageResponseDto> languageResponseDtos);

    UserDto toUserDto(UserResponseDto userResponseDto);

    UserResponseDto toUserResponseDto(UserDto userDto);

    UiMessageDto toUiMessageDto(UiMessageResponse uiMessageResponseDto);
    List<UiMessageDto> toUiMessageDtoList(List<UiMessageResponse> uiMessageResponseDtos);
}
