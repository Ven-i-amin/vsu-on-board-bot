package ru.vsu.core.component.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import ru.vsu.core.model.dto.GroupDto;
import ru.vsu.core.model.entity.Group;
import ru.vsu.core.util.LocalizationUtil;

import java.util.List;

@Mapper(componentModel = "spring", uses = LocalizationUtil.class)
public interface GroupMapper {
    GroupMapper INSTANCE = Mappers.getMapper(GroupMapper.class);

    GroupDto toDto(Group group);

    Group toEntity(GroupDto groupDto);

    List<GroupDto> toDtoList(List<Group> groups);

    List<Group> toEntityList(List<GroupDto> groupDtos);
}
