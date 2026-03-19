package ru.vsu.core.service;

import ru.vsu.core.model.dto.GroupLocalizedDto;

import java.util.List;

public interface LocalizedGroupService {
    GroupLocalizedDto findById(String groupId, String languageCode);
    GroupLocalizedDto findById(String groupId, String languageCode, int depth);
    List<GroupLocalizedDto> findInnerByParentId(String parentId, String languageCode);
    List<GroupLocalizedDto> findByParentIds(List<String> parentIds, String languageCode);
    GroupLocalizedDto findStartGroup(String languageCode);
    GroupLocalizedDto findStartGroup(String languageCode, int depth);
}
