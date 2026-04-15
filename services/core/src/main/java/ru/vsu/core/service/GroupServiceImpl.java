package ru.vsu.core.service;

import lombok.AllArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vsu.core.component.mapper.GroupMapper;
import ru.vsu.core.model.dto.GroupDto;
import ru.vsu.core.model.dto.GroupWithQuestionsDto;
import ru.vsu.core.model.dto.GroupResponse;
import ru.vsu.core.model.entity.Group;
import ru.vsu.core.model.request.GroupRequest;
import ru.vsu.core.model.request.GroupTitleRequest;
import ru.vsu.core.repository.GroupRepository;
import ru.vsu.core.service.GroupService;
import ru.vsu.core.service.QuestionService;
import ru.vsu.core.util.LocalizationUtil;
import ru.vsu.core.util.TransliterationUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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
    private final MongoTemplate mongoTemplate;

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
        GroupDto normalizedGroup = normalizeGroup(group);

        if (normalizedGroup.path().isEmpty()) {
            GroupDto existingRoot = findRoot();
            boolean isAnotherRoot = existingRoot != null
                    && (normalizedGroup.groupId() == null || !existingRoot.groupId().equals(normalizedGroup.groupId()));
            if (isAnotherRoot) {
                throw new IllegalStateException("Root group already exists");
            }
        }

        return groupMapper.toDto(groupRepository.save(groupMapper.toEntity(normalizedGroup)));
    }

    @Override
    public GroupDto save(GroupRequest group) {
        if (group.parentName() == null) {
            throw new IllegalArgumentException("Group must have a parent");
        }

        if (!LocalizationUtil.hasDefaultLanguage(group.title())) {
            throw new IllegalArgumentException("Group must have a default language");
        }

        String russianTitle = localize(group.title(), DEFAULT_LANGUAGE_CODE);
        Group savingGroup = groupMapper.toEntity(group, russianTitle);
        GroupDto parentGroup = findByName(group.parentName());
        if (parentGroup == null) {
            throw new IllegalArgumentException("Parent group not found");
        }
        savingGroup.setPath(buildPath(parentGroup));
        savingGroup.setDepthLevel(savingGroup.getPath().size());
        return saveWithUniqueName(savingGroup);
    }

    @Override
    @Transactional
    public GroupDto updateTitle(String name, GroupTitleRequest groupTitle) {
        if (!LocalizationUtil.hasDefaultLanguage(groupTitle.title())) {
            throw new IllegalArgumentException("Group must have a default language");
        }

        Group group = groupRepository.findByName(name).orElseThrow(IllegalArgumentException::new);

        String oldGroupName = localize(group.getTitle(), DEFAULT_LANGUAGE_CODE);
        String newGroupName = localize(groupTitle.title(), DEFAULT_LANGUAGE_CODE);

        group.setTitle(groupTitle.title());

        if (!oldGroupName.equals(newGroupName)) {
            group.setName(TransliterationUtil.transliterate(newGroupName));
        }

        GroupDto savedGroup = saveWithUniqueName(group);

        if (!name.equals(savedGroup.name())) {
            updateDescendantPaths(name, savedGroup.name());

            questionService.updateGroupName(name, savedGroup.name());
        }

        return savedGroup;
    }

    @Override
    @Transactional
    public void deleteById(String groupId) {
        Group group = groupRepository.findById(groupId).orElseThrow(IllegalArgumentException::new);
        if (group.getPath() == null || group.getPath().isEmpty()) {
            throw new IllegalStateException("Root group cannot be deleted");
        }

        String groupName = group.getName();

        groupRepository.deleteByName(groupName);
        deleteByParentName(groupName);
        questionService.deleteByGroupName(groupName);
    }

    @Override
    @Transactional
    public void deleteByName(String groupName) {
        Group group = groupRepository.findByName(groupName).orElseThrow(IllegalArgumentException::new);
        if (group.getPath() == null || group.getPath().isEmpty()) {
            throw new IllegalStateException("Root group cannot be deleted");
        }

        groupRepository.deleteByName(groupName);
        deleteByParentName(groupName);
        questionService.deleteByGroupName(groupName);
    }

    @Override
    public List<GroupDto> findByParentName(String parentName) {
        return groupMapper.toDtoList(mongoTemplate.find(queryByLastPathElement(parentName), Group.class));
    }

    @Override
    public List<GroupDto> findByParentNames(List<String> parentNames) {
        return groupMapper.toDtoList(mongoTemplate.find(queryByLastPathElementIn(parentNames), Group.class));
    }

    @Override
    public GroupDto findRoot() {
        List<Group> roots = mongoTemplate.find(rootQuery(), Group.class);
        if (roots.isEmpty()) {
            return null;
        }

        Group root = roots.stream()
                .filter(group -> ROOT_GROUP_NAME.equals(group.getName()))
                .findFirst()
                .or(() -> roots.stream().findFirst())
                .orElse(null);

        return groupMapper.toDto(root);
    }

    @Override
    public List<GroupResponse> findRootGroup(int depth) {
        GroupDto group = findRoot();

        return findTreeByName(group.name(), depth);
    }

    @Override
    public GroupDto createRootIfMissing() {
        GroupDto existingRoot = findRoot();
        if (existingRoot != null) {
            return existingRoot;
        }

        return save(
                GroupDto.builder()
                        .name(ROOT_GROUP_NAME)
                        .title(buildRootTitle())
                        .path(List.of())
                        .build()
        );
    }

    @Override
    public List<GroupResponse> findTreeByName(String name, int depth) {
        Aggregation rootNodeAggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("name").is(name)),
                Aggregation.project(
                                "name",
                                "title",
                                "depthLevel",
                                "path"
                        )
                        .and(
                                ArrayOperators.ArrayElemAt.arrayOf("path").elementAt(-1)
                        ).as("parentName"),
                Aggregation.lookup(
                        "questions",
                        "name",
                        "groupName",
                        "questions"
                )
        );

        GroupWithQuestionsDto root = mongoTemplate
                .aggregate(rootNodeAggregation, "groups", GroupWithQuestionsDto.class)
                .getUniqueMappedResult();

        if (root == null) {
            return List.of();
        }

        Long rootDepth = root.depthLevel();

        Aggregation treeNodeAggregation = Aggregation.newAggregation(
                Aggregation.match(
                        Criteria.where("path." + rootDepth).is(root.name())
                                .and("depthLevel").lte(rootDepth + depth)
                ),
                Aggregation.project(
                                "name",
                                "title",
                                "depthLevel",
                                "path"
                        )
                        .and(
                                ArrayOperators.ArrayElemAt.arrayOf("path").elementAt(-1)
                        ).as("parentName"),
                Aggregation.lookup(
                        "questions",
                        "name",
                        "groupName",
                        "questions"
                )
        );

        List<GroupWithQuestionsDto> groupList = mongoTemplate
                .aggregate(treeNodeAggregation, "groups", GroupWithQuestionsDto.class)
                .getMappedResults();

        return groupMapper.toResponse(root, groupList);
    }

    private Map<String, String> buildRootTitle() {
        Map<String, String> title = new LinkedHashMap<>();

        title.put(
                "ru",
                ROOT_TITLE_RU
        );

        return title;
    }

    private GroupDto saveWithUniqueName(Group group) {
        String baseName = group.getName();
        int suffix = 0;

        while (true) {
            try {
                return groupMapper.toDto(groupRepository.save(group));
            } catch (DuplicateKeyException exception) {
                suffix++;
            }

            group.setName(baseName + "-" + suffix);
        }
    }

    private GroupDto normalizeGroup(GroupDto group) {
        if (group.path() != null) {
            return group;
        }

        if (group.parentName() == null) {
            return GroupDto.builder()
                    .groupId(group.groupId())
                    .name(group.name())
                    .title(group.title())
                    .parentName(null)
                    .path(List.of())
                    .build();
        }

        GroupDto parentGroup = findByName(group.parentName());
        if (parentGroup == null) {
            throw new IllegalArgumentException("Parent group not found");
        }

        return GroupDto.builder()
                .groupId(group.groupId())
                .name(group.name())
                .title(group.title())
                .parentName(group.parentName())
                .path(buildPath(parentGroup))
                .build();
    }

    private List<String> buildPath(GroupDto parentGroup) {
        Stream<String> basePath = parentGroup.path() == null
                ? Stream.empty()
                : parentGroup.path().stream();

        return Stream.concat(basePath, Stream.of(parentGroup.name()))
                .toList();
    }

    private Query rootQuery() {
        return new Query(new Criteria().orOperator(
                Criteria.where("path").exists(false),
                Criteria.where("path").size(0)
        ));
    }

    private Query queryByLastPathElement(String parentName) {
        return new BasicQuery(new org.bson.Document(
                "$expr",
                new org.bson.Document(
                        "$eq",
                        List.of(
                                new org.bson.Document("$arrayElemAt", List.of("$path", -1)),
                                parentName
                        )
                )
        ));
    }

    private Query queryByLastPathElementIn(List<String> parentNames) {
        return new BasicQuery(new org.bson.Document(
                "$expr",
                new org.bson.Document(
                        "$in",
                        List.of(
                                new org.bson.Document("$arrayElemAt", List.of("$path", -1)),
                                parentNames
                        )
                )
        ));
    }

    private void deleteByParentName(String parentName) {
        mongoTemplate.remove(queryByLastPathElement(parentName), Group.class);
    }

    private void updateDescendantPaths(String oldName, String newName) {
        mongoTemplate.getCollection("groups").updateMany(
                new org.bson.Document("path", oldName),
                List.of(
                        new org.bson.Document(
                                "$set",
                                new org.bson.Document(
                                        "path",
                                        new org.bson.Document(
                                                "$map",
                                                new org.bson.Document("input", "$path")
                                                        .append("as", "pathItem")
                                                        .append(
                                                                "in",
                                                                new org.bson.Document(
                                                                        "$cond",
                                                                        List.of(
                                                                                new org.bson.Document("$eq", List.of("$$pathItem", oldName)),
                                                                                newName,
                                                                                "$$pathItem"
                                                                        )
                                                                )
                                                        )
                                        )
                                )
                        )
                )
        );
    }
}
