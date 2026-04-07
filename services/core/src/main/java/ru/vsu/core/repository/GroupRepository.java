package ru.vsu.core.repository;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import ru.vsu.core.model.dto.GroupNodeDto;
import ru.vsu.core.model.entity.Group;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends MongoRepository<Group, String> {
    Optional<Group> findFirstByParentNameIsNullOrderByNameAsc();
    List<Group> findAllByParentNameIsNull();
    List<Group> findByParentName(String parentName);
    List<Group> findByParentNameIn(java.util.Collection<String> parentNames);

    Optional<Group> findByName(String name);

    @Aggregation(pipeline = {
            "{ $match: { _id: ?0 } }",
            """
            { $graphLookup: {
                from: 'groups',
                startWith: '$name',
                connectFromField: 'name',
                connectToField: 'parentName',
                as: 'innerGroups',
                maxDepth: ?1,
                depthField: 'level'
            } }
            """,
            """
            { $addFields: {
                allGroups: { $concatArrays: [["$$ROOT"], "$innerGroups"] }
            } }
            """,
            "{ $unwind: '$allGroups' }",
            "{ $replaceRoot: { newRoot: '$allGroups' } }",
            """
            { $addFields: {
                level: { $ifNull: ['$level', -1] }
            } }
            """,
            """
            { $lookup: {
                from: 'questions',
                localField: 'name',
                foreignField: 'groupName',
                as: 'questions'
            } }
            """,
            """
            { $project: {
                _id: 0,
                groupId: '$_id',
                name: 1,
                title: 1,
                parentName: 1,
                level: 1,
                questions: {
                    $map: {
                        input: '$questions',
                        as: 'question',
                        in: {
                            questionId: '$$question._id',
                            name: '$$question.name',
                            parent: '$$question.groupName',
                            title: '$$question.title',
                            text: '$$question.text'
                        }
                    }
                }
            } }
            """,
            "{ $sort: { level: 1, parentName: 1, name: 1 } }"
    })
    List<GroupNodeDto> findTreeNodeByGroupId(String groupId, int depth);

    @Aggregation(pipeline = {
            "{ $match: { name: ?0 } }",
            """
            { $graphLookup: {
                from: 'groups',
                startWith: '$name',
                connectFromField: 'name',
                connectToField: 'parentName',
                as: 'innerGroups',
                maxDepth: ?1,
                depthField: 'level'
            } }
            """,
            """
            { $addFields: {
                allGroups: { $concatArrays: [["$$ROOT"], "$innerGroups"] }
            } }
            """,
            "{ $unwind: '$allGroups' }",
            "{ $replaceRoot: { newRoot: '$allGroups' } }",
            """
            { $addFields: {
                level: { $ifNull: ['$level', -1] }
            } }
            """,
            """
            { $lookup: {
                from: 'questions',
                localField: 'name',
                foreignField: 'groupName',
                as: 'questions'
            } }
            """,
            """
            { $project: {
                _id: 0,
                groupId: '$_id',
                name: 1,
                title: 1,
                parentName: 1,
                level: 1,
                questions: {
                    $map: {
                        input: '$questions',
                        as: 'question',
                        in: {
                            questionId: '$$question._id',
                            name: '$$question.name',
                            parent: '$$question.groupName',
                            title: '$$question.title',
                            text: '$$question.text'
                        }
                    }
                }
            } }
            """,
            "{ $sort: { level: 1, parentName: 1, name: 1 } }"
    })
    List<GroupNodeDto> findTreeByName(String name, int depth);

    void deleteByName(String name);

    void deleteByParentName(String parentName);
}
