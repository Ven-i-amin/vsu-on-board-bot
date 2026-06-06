package ru.vsu.core.controller.bot;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.vsu.contract.model.response.GroupResponseDto;
import ru.vsu.core.component.mapper.ResponseMapper;
import ru.vsu.core.model.dto.GroupDto;
import ru.vsu.core.service.business.GroupService;

import java.util.List;

@RestController
@RequestMapping("/group")
@AllArgsConstructor
public class GroupController {
    private final GroupService groupService;
    private final ResponseMapper responseMapper;

    @GetMapping
    public List<GroupResponseDto> getAllGroups() {
        return responseMapper.toResponseList(groupService.findAll());
    }

    @GetMapping("/{groupName}")
    public GroupResponseDto getGroup(@PathVariable String groupName) {
        GroupDto group = groupService.findByName(groupName);
        if (group == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return responseMapper.toResponse(group);
    }

    @GetMapping("/root")
    public GroupResponseDto getRootGroup() {
        GroupDto root = groupService.findRoot();
        if (root == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return responseMapper.toResponse(root);
    }

    @GetMapping("/{groupName}/children")
    public List<GroupResponseDto> getGroupChildren(@PathVariable String groupName) {
        return groupService.findDirectChildren(groupName).stream()
                .map(responseMapper::toResponse)
                .toList();
    }

    @PostMapping("/start")
    @ResponseStatus(HttpStatus.CREATED)
    public GroupResponseDto createStartGroup() {
        return responseMapper.toResponse(groupService.createRootIfMissing());
    }
}