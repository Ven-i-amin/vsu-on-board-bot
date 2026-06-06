package ru.vsu.core.controller.bot;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.vsu.contract.model.response.QuestionResponseDto;
import ru.vsu.core.component.mapper.ResponseMapper;
import ru.vsu.core.model.dto.QuestionFileDto;
import ru.vsu.core.service.business.QuestionService;
import ru.vsu.core.service.storage.QuestionFileService;

import java.util.List;

@RestController
@RequestMapping("/question")
@AllArgsConstructor
public class QuestionController {
    private final QuestionService questionService;
    private final QuestionFileService questionFileService;
    private final ResponseMapper responseMapper;

    @GetMapping("/group/{groupName}")
    public List<QuestionResponseDto> getByGroup(@PathVariable String groupName) {
        return questionService.findByParentGroupName(groupName).stream()
                .map(responseMapper::toResponse)
                .toList();
    }

    @PostMapping("/{questionName}/fixate")
    public void fixate(@PathVariable String questionName) {
        questionService.incrementUsing(questionName);
    }

    @GetMapping("/{questionId}/files")
    public List<QuestionFileDto> getFiles(@PathVariable String questionId) {
        List<String> fileHashes = questionService.findFileHashesByQuestionId(questionId);
        return questionFileService.getAll(fileHashes);
    }
}