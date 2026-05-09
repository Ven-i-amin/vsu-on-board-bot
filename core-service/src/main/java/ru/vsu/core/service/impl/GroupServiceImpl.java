package ru.vsu.core.service.impl;

import lombok.AllArgsConstructor;
import org.bson.Document;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
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
import ru.vsu.core.service.QuestionService;
import ru.vsu.core.util.LocalizationUtil;
import ru.vsu.core.util.TransliterationUtil;

import java.util.LinkedHashMap;
import java.util.ArrayList;
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
    public GroupDto save(GroupRequest group) {
        if (group.parentName() == null) {
            throw new IllegalArgumentException("Group must have a parent");
        }

        if (!LocalizationUtil.hasDefaultLanguage(group.title())) {
            throw new IllegalArgumentException("Group must have a default language");
        }

        String russianTitle = localize(group.title(), DEFAULT_LANGUAGE_CODE);
        Group savingGroup = groupMapper.toEntity(group, russianTitle);
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
            Query query = new Query(Criteria.where("parentName").is(name));
            Update update = new Update().set("parentName", savedGroup.name());

            mongoTemplate.updateMulti(query, update, Group.class);

            questionService.updateGroupName(name, savedGroup.name());
        }

        return savedGroup;
    }

    @Override
    @Transactional
    public void deleteById(String groupId) {
        Group group = groupRepository.findById(groupId).orElseThrow(IllegalArgumentException::new);
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

        return groupMapper.toDto(root);
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
    public GroupTreeDto findTreeByName(String name, int depth) {
        Aggregation aggregation = getTreeAggregation(name, depth);
        List<GroupNodeDto> nodes = new ArrayList<>(
                mongoTemplate.aggregate(aggregation, "groups", GroupNodeDto.class).getMappedResults()
        );

        if (nodes.isEmpty()) {
            return null;
        }

        GroupNodeDto root = findRootByName(nodes, name);
        nodes.remove(root);

        return buildTree(root, nodes);
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
    
    private Aggregation getTreeAggregation(String name, int depth) {
        return Aggregation.newAggregation(

                // $match
                Aggregation.match(Criteria.where("name").is(name)),

                // $graphLookup
                context -> new Document("$graphLookup",
                        new Document("from", "groups")
                                .append("startWith", "$name")
                                .append("connectFromField", "name")
                                .append("connectToField", "parentName")
                                .append("as", "innerGroups")
                                .append("maxDepth", depth)
                                .append("depthField", "level")
                ),

                // $addFields allGroups
                context -> new Document("$addFields",
                        new Document("allGroups",
                                new Document("$concatArrays",
                                        List.of(List.of("$$ROOT"), "$innerGroups")
                                )
                        )
                ),

                // $unwind
                Aggregation.unwind("allGroups"),

                // $replaceRoot
                context -> new Document("$replaceRoot",
                        new Document("newRoot", "$allGroups")
                ),

                // level default
                context -> new Document("$addFields",
                        new Document("level",
                                new Document("$ifNull", List.of("$level", -1))
                        )
                ),

                // $lookup questions
                context -> new Document("$lookup",
                        new Document("from", "questions")
                                .append("localField", "name")
                                .append("foreignField", "groupName")
                                .append("as", "questions")
                ),

                // $project
                context -> new Document("$project",
                        new Document("_id", 0)
                                .append("groupId", "$_id")
                                .append("name", 1)
                                .append("title", 1)
                                .append("parentName", 1)
                                .append("level", 1)
                                .append("questions",
                                        new Document("$map",
                                                new Document("input", "$questions")
                                                        .append("as", "question")
                                                        .append("in",
                                                                new Document("questionId", "$$question._id")
                                                                        .append("name", "$$question.name")
                                                                        .append("parent", "$$question.groupName")
                                                                        .append("title", "$$question.title")
                                                                        .append("text", "$$question.text")
                                                        )
                                        )
                                )
                ),

                // $sort
                Aggregation.sort(Sort.by(
                        Sort.Order.asc("level"),
                        Sort.Order.asc("parentName"),
                        Sort.Order.asc("name")
                ))
        );
    }
}
