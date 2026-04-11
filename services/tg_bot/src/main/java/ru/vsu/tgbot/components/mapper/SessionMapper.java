package ru.vsu.tgbot.components.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.model.entity.Session;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface SessionMapper {
    SessionMapper INSTANCE = Mappers.getMapper(SessionMapper.class);

    @Mapping(target = "start", ignore = true)
    @Mapping(target = "update", ignore = true)
    SessionDto sessionToSessionDto(Session session);

    @BeanMapping(ignoreUnmappedSourceProperties = {"start", "update", "uiMessages"})
    Session sessionDtoToSession(SessionDto sessionDto);
}
