package ru.vsu.core.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.vsu.core.model.response.GroupResponseDto;
import ru.vsu.core.service.LocalizedGroupService;

import java.util.List;

import static ru.vsu.core.service.impl.LocalizedGroupServiceImpl.DEFAULT_LANGUAGE_CODE;

@RestController
@RequestMapping("/group")
@AllArgsConstructor
public class GroupController {
    private final LocalizedGroupService localizedGroupService;
    private final ResponseMapper responseMapper;

    @GetMapping("/{groupId}")
    public GroupResponseDto getGroup(
            @PathVariable String groupId,
            @RequestParam("lang") String language,
            @RequestParam(value = "depth", defaultValue = "0") Integer depth
    ) {
        GroupResponseDto group = responseMapper.toResponse(localizedGroupService.findById(groupId, language, depth));
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
        return localizedGroupService.findInnerByParentId(groupId, language).stream()
                .map(responseMapper::toResponse)
                .toList();
    }

    @PostMapping("/list")
    public List<GroupResponseDto> getInnerGroupsForEachGroup(
            @RequestBody List<String> groupIds,
            @RequestParam("lang") String language
    ) {
        return localizedGroupService.findByParentIds(groupIds, language).stream()
                .map(responseMapper::toResponse)
                .toList();
    }

    @GetMapping("/start")
    public GroupResponseDto getStartGroup(@RequestParam(value = "lang", required = false) String language) {
        String resolvedLanguage = language == null || language.isBlank() ? DEFAULT_LANGUAGE_CODE : language;
        GroupResponseDto group = responseMapper.toResponse(localizedGroupService.findStartGroup(resolvedLanguage, 3));
        if (group == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return group;
    }
}
