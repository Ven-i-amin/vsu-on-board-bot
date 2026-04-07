package ru.vsu.core.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;
import ru.vsu.core.component.mapper.GroupMapper;
import ru.vsu.core.model.dto.GroupDto;
import ru.vsu.core.model.dto.GroupNodeDto;
import ru.vsu.core.model.dto.GroupTreeDto;
import ru.vsu.core.model.entity.Group;
import ru.vsu.core.model.request.GroupRequest;
import ru.vsu.core.model.request.GroupTitleRequest;
import ru.vsu.core.repository.GroupRepository;
import ru.vsu.core.service.GroupService;
import ru.vsu.core.service.LanguageService;
import ru.vsu.core.service.QuestionService;
import ru.vsu.core.util.LocalizationUtil;
import ru.vsu.core.util.TransliterationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static ru.vsu.core.util.GroupTreeUtil.*;
import static ru.vsu.core.util.LocalizationUtil.DEFAULT_LANGUAGE_CODE;
import static ru.vsu.core.util.LocalizationUtil.localize;

@Service
@AllArgsConstructor
public class GroupServiceImpl implements GroupService {
    private static final String ROOT_GROUP_NAME = "main-menu";
    private static final String ROOT_TITLE_RU = "Главное меню";

    private final GroupRepository groupRepository;
    private final GroupMapper groupMapper;
    private final QuestionService questionService;

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
        if (group.parentName() == null) {
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
    public void save(GroupRequest group) {
        if (group.parentName() == null) {
            throw new IllegalArgumentException("Group must have a parent");
        }

        if (LocalizationUtil.hasDefaultLanguage(group.title())) {
            throw new IllegalArgumentException("Group must have a default language");
        }

        String russianTitle = localize(group.title(), DEFAULT_LANGUAGE_CODE);
        Group savingGroup = groupMapper.toEntity(group, russianTitle);
        saveWithUniqueName(savingGroup);
    }

    @Override
    public void updateTitle(String name, GroupTitleRequest groupTitle) {
        Group group = groupRepository.findById(name).orElseThrow(IllegalArgumentException::new);

        group.setTitle(groupTitle.title());

        String oldGroupName = localize(group.getTitle(), DEFAULT_LANGUAGE_CODE);
        String newGroupName = localize(groupTitle.title(), DEFAULT_LANGUAGE_CODE);

        if (!oldGroupName.equals(newGroupName)) {
            group.setName(TransliterationUtil.transliterate(newGroupName));
        }

        saveWithUniqueName(group);
    }

    @Override
    @Transactional
    public void deleteById(String groupId) {
        Group group = groupRepository.findByName(groupId).orElseThrow(IllegalArgumentException::new);
        if (group.getParentName() == null) {
            throw new IllegalStateException("Root group cannot be deleted");
        }

        String groupName = group.getName();

        groupRepository.deleteByName(groupName);
        groupRepository.deleteByParentName(groupName);
        questionService.deleteByGroupName(groupName);
    }

    @Override
    @Transactional
    public void deleteByName(String groupName) {
        Group group = groupRepository.findByName(groupName).orElseThrow(IllegalArgumentException::new);
        if (group.getParentName() == null) {
            throw new IllegalStateException("Root group cannot be deleted");
        }

        groupRepository.deleteByName(groupName);
        groupRepository.deleteByParentName(groupName);
        questionService.deleteByGroupName(groupName);
    }

    @Override
    public List<GroupDto> findByParentName(String parentName) {
        return groupMapper.toDtoList(groupRepository.findByParentName(parentName));
    }

    @Override
    public List<GroupDto> findByParentNames(List<String> parentNames) {
        return groupMapper.toDtoList(groupRepository.findByParentNameIn(parentNames));
    }

    @Override
    public GroupDto findRoot() {
        List<Group> roots = groupRepository.findAllByParentNameIsNull();
        if (roots.isEmpty()) {
            return null;
        }

        Group root = roots.stream()
                .filter(group -> ROOT_GROUP_NAME.equals(group.getName()))
                .findFirst()
                .or(() -> roots.stream().findFirst())
                .orElse(null);

        return root == null ? null : groupMapper.toDto(root);
    }

    @Override
    public GroupTreeDto findRootGroup(int depth) {
        GroupDto group = findRoot();

        assert group != null;
        return findTreeByName(group.name(), depth);
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
                .parentName(null)
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

        title.put(
                "ru",
                ROOT_TITLE_RU
        );

        title.put(
                "en",
                "Main Menu"
        );

        return title;
    }

    private Group saveWithUniqueName(Group group) {
        String baseName = group.getName();
        int suffix = 0;

        while (true) {
            try {
                return groupRepository.save(group);
            } catch (DuplicateKeyException exception) {
                suffix++;
            }

            group.setName(suffix == 0 ? baseName : baseName + "-" + suffix);
        }
    }
}
