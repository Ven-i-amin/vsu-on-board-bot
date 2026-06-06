package ru.vsu.core.component.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.vsu.core.model.dto.GroupDto;
import ru.vsu.core.model.entity.Group;
import ru.vsu.core.model.request.GroupRequest;
import ru.vsu.core.util.TransliterationUtil;

import java.util.List;

@Mapper(componentModel = "spring", imports = TransliterationUtil.class)
public interface GroupMapper {
    GroupDto toDto(Group group);

    Group toEntity(GroupDto groupDto);

    @Mapping(target = "groupId", ignore = true)
    @Mapping(target = "parents", ignore = true)
    @Mapping(target = "name", expression = "java(TransliterationUtil.transliterate(russianTitle))")
    Group toEntity(GroupRequest groupRequest, @Context String russianTitle);

    List<GroupDto> toDtoList(List<Group> groups);

    List<Group> toEntityList(List<GroupDto> groupDtos);
}