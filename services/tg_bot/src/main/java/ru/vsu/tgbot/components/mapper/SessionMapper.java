package ru.vsu.tgbot.components.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.model.entity.Session;

@Mapper()
public interface SessionMapper {
    SessionMapper INSTANCE = Mappers.getMapper(SessionMapper.class);

    SessionDto sessionToSessionDto(Session session);
    Session sessionDtoToSession(SessionDto sessionDto);
}
