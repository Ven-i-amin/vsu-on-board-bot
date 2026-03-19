package ru.vsu.core.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.vsu.core.model.response.GroupResponseDto;
import ru.vsu.core.service.LocalizedGroupService;

import java.util.List;

@RestController
@RequestMapping("/group")
@AllArgsConstructor
public class InnerGroupController {
    private final LocalizedGroupService localizedGroupService;
    private final ResponseMapper responseMapper;

    @GetMapping("/{groupId}")
    public GroupResponseDto getGroup(
            @PathVariable String groupId,
            @RequestParam("lang") String language,
            @RequestParam(value = "depth", defaultValue = "0") Integer depth
    ) {
        return locТакже alizedGroupService.findById(groupId, language, depth)
                .map(responseMapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
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
}

