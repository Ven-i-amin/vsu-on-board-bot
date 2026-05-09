package ru.vsu.core.component.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.vsu.core.model.dto.UiMessageDto;
import ru.vsu.core.model.entity.UiMessage;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UiMessageMapper {
    UiMessageMapper INSTANCE = Mappers.getMapper(UiMessageMapper.class);

    UiMessageDto toDto(UiMessage uiMessage);

    UiMessage toEntity(UiMessageDto uiMessageDto);

    List<UiMessageDto> toDtoList(List<UiMessage> uiMessages);
}
