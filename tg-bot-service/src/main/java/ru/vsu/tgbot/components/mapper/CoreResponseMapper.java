package ru.vsu.tgbot.components.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.vsu.contract.model.response.GroupResponseDto;
import ru.vsu.contract.model.response.LanguageResponseDto;
import ru.vsu.contract.model.response.QuestionResponseDto;
import ru.vsu.contract.model.response.UiMessageResponseDto;
import ru.vsu.contract.model.response.UserResponseDto;
import ru.vsu.tgbot.model.dto.*;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CoreResponseMapper {

    default GroupDto toGroupDto(GroupResponseDto dto) {
        if (dto == null) return null;
        return GroupDto.builder()
                .name(dto.name())
                .title(dto.title())
                .parents(dto.parents() != null ? new ArrayList<>(dto.parents()) : new ArrayList<>())
                .innerGroups(new ArrayList<>())
                .questions(new ArrayList<>())
                .build();
    }

    default List<GroupDto> toGroupDtoList(List<GroupResponseDto> dtos) {
        if (dtos == null) return null;
        List<GroupDto> result = new ArrayList<>(dtos.size());
        for (GroupResponseDto dto : dtos) {
            result.add(toGroupDto(dto));
        }
        return result;
    }

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
