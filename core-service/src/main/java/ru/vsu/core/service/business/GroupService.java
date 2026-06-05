package ru.vsu.core.service;

import ru.vsu.core.model.dto.GroupDto;
import ru.vsu.core.model.request.GroupRequest;
import ru.vsu.core.model.request.GroupTitleRequest;

import java.util.List;

public interface GroupService {
    List<GroupDto> findAll();

    GroupDto findById(String groupId);

    GroupDto findByName(String groupName);

    GroupDto save(GroupDto group);

    GroupDto save(GroupRequest group);

    GroupDto updateTitle(String name, GroupTitleRequest groupTitle);

    void deleteById(String groupId);

    void deleteByName(String groupName);

    GroupDto findRoot();

    GroupDto createRootIfMissing();

    List<GroupDto> findDirectChildren(String parentName);
}