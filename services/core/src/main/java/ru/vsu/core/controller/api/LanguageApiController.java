package ru.vsu.core.controller.api;


import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vsu.contract.model.response.LanguageResponseDto;
import ru.vsu.core.component.mapper.ResponseMapper;
import ru.vsu.core.service.LanguageService;

import java.util.List;

@RestController
@RequestMapping("/api/language")
@org.springframework.web.bind.annotation.CrossOrigin(origins = "http://localhost:5173")
@AllArgsConstructor
public class LanguageApiController {
    private final LanguageService languageService;
    private final ResponseMapper responseMapper;

    @GetMapping
    public List<LanguageResponseDto> getLanguages() {
        return languageService.findAll()
                .stream()
                .map(responseMapper::toResponse)
                .toList();
    }
}
