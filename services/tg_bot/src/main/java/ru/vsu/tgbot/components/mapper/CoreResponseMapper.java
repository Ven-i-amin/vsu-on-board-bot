package ru.vsu.tgbot.components.mapper;

import org.mapstruct.Mapper;
import ru.vsu.tgbot.model.dto.GroupDto;
import ru.vsu.tgbot.model.dto.LanguageDto;
import ru.vsu.tgbot.model.dto.QuestionDto;
import ru.vsu.tgbot.model.dto.UserDto;
import ru.vsu.tgbot.model.response.GroupResponseDto;
import ru.vsu.tgbot.model.response.LanguageResponseDto;
import ru.vsu.tgbot.model.response.QuestionResponseDto;
import ru.vsu.tgbot.model.response.UserResponseDto;

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
}
