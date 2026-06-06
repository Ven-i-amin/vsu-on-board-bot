package ru.vsu.core.service.business;

import lombok.AllArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vsu.core.component.mapper.GroupMapper;
import ru.vsu.core.model.dto.GroupDto;
import ru.vsu.core.model.entity.Group;
import ru.vsu.core.model.request.GroupRequest;
import ru.vsu.core.model.request.GroupTitleRequest;
import ru.vsu.core.repository.mongo.GroupRepository;
import ru.vsu.core.util.LocalizationUtil;
import ru.vsu.core.util.TransliterationUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        return groupMapper.toDto(groupRepository.save(groupMapper.toEntity(group)));
    }

    @Override
    public GroupDto save(GroupRequest group) {
        if (group.parentName() == null) {
            throw new IllegalArgumentException("Group must have a parent");
        }

        if (!LocalizationUtil.hasDefaultLanguage(group.title())) {
            throw new IllegalArgumentException("Group must have a default language");
        }

        GroupDto parent = groupRepository.findByName(group.parentName())
                .map(groupMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Parent group not found: " + group.parentName()));

        String russianTitle = localize(group.title(), DEFAULT_LANGUAGE_CODE);
        Group entity = groupMapper.toEntity(group, russianTitle);
        entity.setParents(buildChildParents(parent));
        return saveWithUniqueName(entity);
    }

    @Override
    @Transactional
    public GroupDto updateTitle(String name, GroupTitleRequest groupTitle) {
        if (!LocalizationUtil.hasDefaultLanguage(groupTitle.title())) {
            throw new IllegalArgumentException("Group must have a default language");
        }

        Group group = groupRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Group not found: " + name));

        String oldTitleRu = localize(group.getTitle(), DEFAULT_LANGUAGE_CODE);
        String newTitleRu = localize(groupTitle.title(), DEFAULT_LANGUAGE_CODE);

        group.setTitle(groupTitle.title());

        if (!oldTitleRu.equals(newTitleRu)) {
            group.setName(TransliterationUtil.transliterate(newTitleRu));
        }

        GroupDto savedGroup = saveWithUniqueName(group);

        if (!name.equals(savedGroup.name())) {
            List<Group> descendants = groupRepository.findByParentsContaining(name);
            List<Group> updated = descendants.stream()
                    .map(g -> {
                        List<String> newParents = g.getParents().stream()
                                .map(p -> p.equals(name) ? savedGroup.name() : p)
                                .toList();
                        g.setParents(newParents);
                        return g;
                    })
                    .toList();
            groupRepository.saveAll(updated);
            questionService.updateGroupName(name, savedGroup.name());
        }

        return savedGroup;
    }

    @Override
    @Transactional
    public void deleteById(String groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found: " + groupId));
        if (group.getParents() == null || group.getParents().isEmpty()) {
            throw new IllegalStateException("Root group cannot be deleted");
        }
        deleteGroupAndDescendants(group.getName());
    }

    @Override
    @Transactional
    public void deleteByName(String groupName) {
        Group group = groupRepository.findByName(groupName)
                .orElseThrow(() -> new IllegalArgumentException("Group not found: " + groupName));
        if (group.getParents() == null || group.getParents().isEmpty()) {
            throw new IllegalStateException("Root group cannot be deleted");
        }
        deleteGroupAndDescendants(groupName);
    }

    @Override
    public GroupDto findRoot() {
        List<Group> roots = groupRepository.findByParentsIsEmpty();
        if (roots.isEmpty()) {
            return null;
        }

        return roots.stream()
                .filter(g -> ROOT_GROUP_NAME.equals(g.getName()))
                .findFirst()
                .or(() -> roots.stream().findFirst())
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
                .parents(List.of())
                .build());
    }

    @Override
    public List<GroupDto> findDirectChildren(String parentName) {
        return groupMapper.toDtoList(groupRepository.findDirectChildren(parentName));
    }

    private void deleteGroupAndDescendants(String groupName) {
        List<Group> descendants = groupRepository.findByParentsContaining(groupName);
        List<String> descendantNames = descendants.stream().map(Group::getName).toList();

        questionService.deleteByGroupName(groupName);
        if (!descendantNames.isEmpty()) {
            questionService.deleteByGroupNames(descendantNames);
            groupRepository.deleteByParentsContaining(groupName);
        }
        groupRepository.deleteByName(groupName);
    }

    private List<String> buildChildParents(GroupDto parent) {
        List<String> parents = new ArrayList<>(
                parent.parents() == null ? List.of() : parent.parents()
        );
        parents.add(parent.name());
        return parents;
    }

    private GroupDto saveWithUniqueName(Group group) {
        String baseName = group.getName();
        int suffix = 0;

        while (true) {
            try {
                return groupMapper.toDto(groupRepository.save(group));
            } catch (DuplicateKeyException ignored) {
                suffix++;
            }
            group.setName(baseName + "-" + suffix);
        }
    }

    private Map<String, String> buildRootTitle() {
        Map<String, String> title = new LinkedHashMap<>();
        title.put("ru", ROOT_TITLE_RU);
        title.put("en", "Main Menu");
        return title;
    }
}