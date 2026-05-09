package ru.vsu.core.controller.api;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.vsu.core.component.mapper.UiMessageMapper;
import ru.vsu.core.model.dto.UiMessageDto;
import ru.vsu.core.model.request.UiMessageUpdateRequest;
import ru.vsu.core.service.UiMessageService;

import java.util.List;

@RestController
@RequestMapping("/api/ui-message")
@AllArgsConstructor
public class UiMessageApiController {
    private final UiMessageService uiMessageService;

    @GetMapping
    public List<UiMessageDto> getUiMessages() {
        return uiMessageService.findAll();
    }

    @GetMapping("/{name}")
    public UiMessageDto getUiMessage(@PathVariable String name) {
        return uiMessageService.findByName(name);
    }

    @PatchMapping("/{name}")
    public UiMessageDto updateUiMessage(
            @PathVariable String name,
            @RequestBody UiMessageUpdateRequest uiMessageDto
    ) {
        return uiMessageService.update(name, uiMessageDto);
    }
}
