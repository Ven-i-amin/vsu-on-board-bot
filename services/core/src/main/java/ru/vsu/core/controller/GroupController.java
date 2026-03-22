package ru.vsu.core.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.vsu.core.model.response.GroupResponseDto;
import ru.vsu.core.service.GroupService;

import java.util.List;

@RestController
@RequestMapping("/group")
@AllArgsConstructor
public class GroupController {
    private static final String DEFAULT_LANGUAGE_CODE = "ru";
    private static final String START_GROUP_NAME = "start";

    private final GroupService groupService;
    private final ResponseMapper responseMapper;

    @GetMapping("/{groupId}")
    public GroupResponseDto getGroup(
            @PathVariable String groupId,
            @RequestParam("lang") String language,
            @RequestParam(value = "depth", defaultValue = "0") Integer depth
    ) {
        GroupResponseDto group = responseMapper.toResponse(groupService.findTreeById(groupId, depth));
        if (group == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return group;
    }

    @GetMapping("/{groupId}/inner")
    public List<GroupResponseDto> getInnerGroup(
            @PathVariable String groupId,
            @RequestParam("lang") String language
    ) {
        return groupService.findByParentId(groupId).stream()
                .map(responseMapper::toShallowResponse)
                .toList();
    }

    @PostMapping("/list")
    public List<GroupResponseDto> getInnerGroupsForEachGroup(
            @RequestBody List<String> groupIds,
            @RequestParam("lang") String language
    ) {
        return groupService.findByParentIds(groupIds).stream()
                .map(responseMapper::toShallowResponse)
                .toList();
    }

    @GetMapping("/start")
    public GroupResponseDto getStartGroup(@RequestParam(value = "lang", required = false) String language) {
        String resolvedLanguage = language == null || language.isBlank() ? DEFAULT_LANGUAGE_CODE : language;
        GroupResponseDto group = responseMapper.toResponse(groupService.findTreeByName(START_GROUP_NAME, 3));
        if (group == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return group;
    }
}
