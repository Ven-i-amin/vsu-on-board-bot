package ru.vsu.core.controller.api;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.vsu.contract.model.response.GroupResponseDto;
import ru.vsu.core.component.mapper.ResponseMapper;
import ru.vsu.core.model.dto.GroupDto;
import ru.vsu.core.model.request.GroupRequest;
import ru.vsu.core.model.request.GroupTitleRequest;
import ru.vsu.core.service.GroupService;

import java.util.List;

@RestController
@RequestMapping("/api/group")
@CrossOrigin(origins = "http://localhost:5173")
@AllArgsConstructor
public class GroupApiController {
    private final GroupService groupService;
    private final ResponseMapper responseMapper;

    @GetMapping("/{groupName}")
    public GroupResponseDto getGroup(
            @PathVariable String groupName,
            @RequestParam(value = "depth", defaultValue = "0") Integer depth
    ) {
        List<GroupResponseDto> groups = responseMapper.toResponse(groupService.findTreeByName(groupName, depth));
        if (groups.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return groups.getFirst();
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

        List<GroupResponseDto> groups = responseMapper.toResponse(groupService.findRootGroup(3));
        if (groups.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return groups.getFirst();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GroupDto createGroup(
            @RequestBody @NotNull GroupRequest group
    ) {
        return groupService.save(group);
    }

    @PatchMapping("/{groupName}/title")
    public GroupDto updateGroupTitle(
            @PathVariable String groupName,
            @RequestBody @NotNull GroupTitleRequest groupTitleRequest
    ) {
        return groupService.updateTitle(groupName, groupTitleRequest);
    }

    @DeleteMapping("/{groupName}")
    public void deleteGroup(
            @PathVariable String groupName
    ) {
        groupService.deleteByName(groupName);
    }
}
