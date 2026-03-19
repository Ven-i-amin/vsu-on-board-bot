package ru.vsu.core.service;

import ru.vsu.core.model.dto.GroupDto;

import java.util.List;

public interface GroupService {
    List<GroupDto> findAll();
    GroupDto findById(String groupId);
    GroupDto save(GroupDto group);
    void deleteById(String groupId);
    List<GroupDto> findByParentId(String parentId);
    List<GroupDto> findByParentIds(List<String> parentIds);
}
