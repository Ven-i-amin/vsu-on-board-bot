package ru.vsu.core.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.core.component.mapper.GroupMapper;
import ru.vsu.core.model.dto.GroupDto;
import ru.vsu.core.repository.GroupRepository;
import ru.vsu.core.service.GroupService;

import java.util.List;

@Service
@AllArgsConstructor
public class GroupServiceImpl implements GroupService {
    private final GroupRepository groupRepository;
    private final GroupMapper groupMapper;

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
    public GroupDto save(GroupDto group) {
        return groupMapper.toDto(groupRepository.save(groupMapper.toEntity(group)));
    }

    @Override
    public void deleteById(String groupId) {
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
}
