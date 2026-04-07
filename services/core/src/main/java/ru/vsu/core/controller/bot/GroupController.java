package ru.vsu.core.controller.internal;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.vsu.contract.model.response.GroupResponseDto;
import ru.vsu.core.component.mapper.ResponseMapper;
import ru.vsu.core.model.dto.GroupDto;
import ru.vsu.core.service.GroupService;

import java.util.List;

@RestController
@RequestMapping("/group")
@AllArgsConstructor
public class GroupController {
    private final GroupService groupService;
    private final ResponseMapper responseMapper;

    @GetMapping("/{groupName}")
    public GroupResponseDto getGroup(
            @PathVariable String groupName,
            @RequestParam(value = "depth", defaultValue = "0") Integer depth
    ) {
        GroupResponseDto group = responseMapper.toResponse(groupService.findTreeByName(groupName, depth));
        if (group == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return group;
    }

    @GetMapping("/{groupName}/inner")
    public List<GroupResponseDto> getInnerGroup(@PathVariable String groupName) {
        GroupDto group = groupService.findByName(groupName);
        if (group == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return groupService.findByParentName(groupName).stream()
                .map(responseMapper::toShallowResponse)
                .toList();
    }

    @GetMapping("/start")
    public GroupResponseDto getStartGroup() {
        GroupDto rootGroup = groupService.findRoot();
        if (rootGroup == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        GroupResponseDto group = responseMapper.toResponse(groupService.findRootGroup(3));
        if (group == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return group;
    }

    @PostMapping("/list")
    public List<GroupResponseDto> getInnerGroupsForEachGroup(@RequestBody List<String> groupNames) {
        return groupService.findByParentNames(groupNames).stream()
                .map(responseMapper::toShallowResponse)
                .toList();
    }

    @PostMapping("/start")
    @ResponseStatus(HttpStatus.CREATED)
    public GroupResponseDto createStartGroup() {
        GroupDto rootGroup = groupService.createRootIfMissing();
        GroupResponseDto group = responseMapper.toResponse(groupService.findTreeByName(rootGroup.name(), 3));
        if (group == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return group;
    }
}
