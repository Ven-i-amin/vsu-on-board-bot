package ru.vsu.core.controller.api;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.vsu.contract.model.response.UiMessageResponseDto;
import ru.vsu.core.component.mapper.ResponseMapper;
import ru.vsu.core.model.dto.UiMessageDto;
import ru.vsu.core.model.request.UiMessageUpdateRequest;
import ru.vsu.core.service.UiMessageService;

import java.util.List;

@RestController
@RequestMapping("/api/group")
@AllArgsConstructor
public class UiMessageApiController {
    private final UiMessageService uiMessageService;
    private final ResponseMapper responseMapper;

    @GetMapping
    public List<UiMessageResponseDto> getUiMessages() {
        return uiMessageService.findAll().stream()
                .map(responseMapper::toResponse)
                .toList();
    }

    @GetMapping("/{name}")
    public UiMessageResponseDto getUiMessage(@PathVariable String name) {
        UiMessageDto uiMessage = uiMessageService.findByName(name);
        if (uiMessage == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return responseMapper.toResponse(uiMessage);
    }

    @PatchMapping("/{name}")
    public void updateUiMessage(
            @PathVariable String name,
            @RequestBody UiMessageUpdateRequest uiMessageDto
    ) {
        uiMessageService.update(name, uiMessageDto);
    }
}
