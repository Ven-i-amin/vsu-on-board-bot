package ru.vsu.core.controller.api;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.vsu.core.model.dto.GroupDto;
import ru.vsu.core.model.request.GroupRequest;
import ru.vsu.core.model.request.GroupTitleRequest;
import ru.vsu.core.service.business.GroupService;

import java.util.List;

@RestController
@RequestMapping("/api/group")
@AllArgsConstructor
public class GroupApiController {
    private final GroupService groupService;

    @GetMapping
    public List<GroupDto> getAllGroups() {
        return groupService.findAll();
    }

    @GetMapping("/{groupName}")
    public GroupDto getGroup(@PathVariable String groupName) {
        GroupDto group = groupService.findByName(groupName);
        if (group == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return group;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GroupDto createGroup(@RequestBody @NotNull GroupRequest group) {
        return groupService.save(group);
    }

    @PatchMapping("/{groupName}/title")
    public GroupDto updateGroupTitle(
            @PathVariable String groupName,
            @RequestBody @NotNull GroupTitleRequest groupTitleRequest
    ) {
        return groupService.updateTitle(groupName, groupTitleRequest);
    }

    @GetMapping({"/root", "/start"})
    public GroupDto getRootGroup() {
        GroupDto root = groupService.findRoot();
        if (root == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return root;
    }

    @GetMapping("/{groupName}/children")
    public List<GroupDto> getGroupChildren(@PathVariable String groupName) {
        return groupService.findDirectChildren(groupName);
    }

    @DeleteMapping("/{groupName}")
    public void deleteGroup(@PathVariable String groupName) {
        groupService.deleteByName(groupName);
    }
}