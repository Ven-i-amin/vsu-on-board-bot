package ru.vsu.core.component.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import ru.vsu.core.model.dto.GroupDto;
import ru.vsu.core.model.dto.GroupResponse;
import ru.vsu.core.model.dto.GroupWithQuestionsDto;
import ru.vsu.core.model.entity.Group;
import ru.vsu.core.model.request.GroupRequest;
import ru.vsu.core.util.LocalizationUtil;
import ru.vsu.core.util.TransliterationUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Mapper(componentModel = "spring", uses = LocalizationUtil.class, imports = TransliterationUtil.class)
public interface GroupMapper {
    GroupMapper INSTANCE = Mappers.getMapper(GroupMapper.class);

    @Mapping(
            target = "parentName",
            expression = "java(group.getPath() == null || group.getPath().isEmpty() ? null : group.getPath().get(group.getPath().size() - 1))"
    )
    GroupDto toDto(Group group);

    Group toEntity(GroupDto groupDto);

    @Mapping(target = "groupId", ignore = true)
    @Mapping(target = "name", expression = "java(TransliterationUtil.transliterate(russianTitle))")
    @Mapping(target = "depth", ignore = true)
    @Mapping(target = "path", ignore = true)
    Group toEntity(GroupRequest groupRequest, @Context String russianTitle);

    List<GroupDto> toDtoList(List<Group> groups);

    List<Group> toEntityList(List<GroupDto> groupDtos);

    @Mapping(target = "childrenNames", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
    GroupResponse toDtoWithChildren(GroupWithQuestionsDto group, List<String> childrenNames);

    default List<GroupResponse> toResponse(GroupWithQuestionsDto root, List<GroupWithQuestionsDto> groups) {
        HashMap<String, List<String>> parentsChildren = new HashMap<>();
        parentsChildren.put(root.name(), new ArrayList<>());

        for (GroupWithQuestionsDto node : groups) {
            List<String> parentChildren = parentsChildren.get(node.parentName());

            if (parentChildren == null) {
                parentsChildren.put(node.parentName(), new ArrayList<>(List.of(node.name())));
                continue;
            }

            parentChildren.add(node.name());
        }

        return groups.stream()
                .map(group -> toDtoWithChildren(group, parentsChildren.get(group.name())))
                .toList();
    }
}
