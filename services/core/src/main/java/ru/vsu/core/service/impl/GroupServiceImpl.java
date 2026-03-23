package ru.vsu.core.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.core.component.mapper.GroupMapper;
import ru.vsu.core.model.dto.GroupDto;
import ru.vsu.core.model.dto.GroupNodeDto;
import ru.vsu.core.model.dto.GroupTreeDto;
import ru.vsu.core.repository.GroupRepository;
import ru.vsu.core.service.GroupService;
import ru.vsu.core.service.LanguageService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static ru.vsu.core.util.GroupTreeUtil.*;

@Service
@AllArgsConstructor
public class GroupServiceImpl implements GroupService {
    private static final String ROOT_GROUP_NAME = "main-menu";
    private static final String ROOT_TITLE_EN = "Main menu";

    private final GroupRepository groupRepository;
    private final GroupMapper groupMapper;
    private final LanguageService languageService;

    @Override
    public List<GroupDto> findAll() {
        return groupMapper.toDtoList(groupRepository.findAll());
    }

    @Override
    public GroupDto findById(String groupId) {
        return groupRepository.findById(groupId)
                .map(groupMapper::toDto)
                .orElse(null);
    }

    @Override
    public GroupDto findByName(String name) {
        return groupRepository.findByName(name)
                .map(groupMapper::toDto)
                .orElse(null);
    }

    @Override
    public GroupDto save(GroupDto group) {
        if (group.parentId() == null) {
            GroupDto existingRoot = findRoot();
            boolean isAnotherRoot = existingRoot != null
                    && (group.groupId() == null || !existingRoot.groupId().equals(group.groupId()));
            if (isAnotherRoot) {
                throw new IllegalStateException("Root group already exists");
            }
        }

        return groupMapper.toDto(groupRepository.save(groupMapper.toEntity(group)));
    }

    @Override
    public void deleteById(String groupId) {
        GroupDto group = findById(groupId);
        if (group != null && group.parentId() == null) {
            throw new IllegalStateException("Root group cannot be deleted");
        }

        groupRepository.deleteById(groupId);
    }

    @Override
    public List<GroupDto> findByParentId(String parentId) {
        return groupMapper.toDtoList(groupRepository.findByParentId(parentId));
    }

    @Override
    public List<GroupDto> findByParentIds(List<String> parentIds) {
        return groupMapper.toDtoList(groupRepository.findByParentIdIn(parentIds));
    }

    @Override
    public GroupDto findRoot() {
        return groupRepository.findByParentIdIsNull()
                .map(groupMapper::toDto)
                .orElse(null);
    }

    @Override
    public GroupDto createRootIfMissing() {
        GroupDto existingRoot = findRoot();
        if (existingRoot != null) {
            return existingRoot;
        }

        return save(GroupDto.builder()
                .name(ROOT_GROUP_NAME)
                .title(buildRootTitle())
                .parentId(null)
                .build());
    }

    @Override
    public GroupTreeDto findTreeById(String groupId, int depth) {
        List<GroupNodeDto> nodes = groupRepository.findTreeNodeByGroupId(groupId, depth);

        if (nodes == null || nodes.isEmpty()) {
            return null;
        }

        GroupNodeDto root = findRootById(nodes, groupId);
        nodes.remove(root);

        return buildTree(root, nodes);
    }

    @Override
    public GroupTreeDto findTreeByName(String name, int depth) {
        List<GroupNodeDto> nodes = groupRepository.findTreeByName(name, depth);

        if (nodes == null || nodes.isEmpty()) {
            return null;
        }

        GroupNodeDto root = findRootByName(nodes, name);
        nodes.remove(root);

        return buildTree(root, nodes);
    }

    public List<GroupNodeDto> findNodeById(String groupId, int depth) {
        return groupRepository.findTreeNodeByGroupId(groupId, depth);
    }

    public List<GroupNodeDto> findNodeByName(String name, int depth) {
        return groupRepository.findTreeByName(name, depth);
    }

    private Map<String, String> buildRootTitle() {
        Map<String, String> title = new LinkedHashMap<>();

        languageService.findAll().forEach(language -> title.put(
                language.code(),
                ROOT_TITLE_EN
        ));

        title.putIfAbsent("ru", ROOT_TITLE_EN);
        title.putIfAbsent("en", ROOT_TITLE_EN);
        return title;
    }
}
