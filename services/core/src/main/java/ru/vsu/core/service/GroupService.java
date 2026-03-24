package ru.vsu.core.service;

import ru.vsu.core.model.dto.GroupDto;
import ru.vsu.core.model.dto.GroupNodeDto;
import ru.vsu.core.model.dto.GroupTreeDto;

import java.util.List;

public interface GroupService {
    List<GroupDto> findAll();

    GroupDto findById(String groupId);

    GroupDto findByName(String groupName);

    GroupDto save(GroupDto group);

    void deleteById(String groupId);

    List<GroupDto> findByParentName(String parentName);

    List<GroupDto> findByParentNames(List<String> parentNames);

    GroupDto findRoot();
    GroupTreeDto findRootGroup(int depth);

    GroupDto createRootIfMissing();

    GroupTreeDto findTreeById(String groupId, int depth);

    GroupTreeDto findTreeByName(String groupName, int depth);
}
