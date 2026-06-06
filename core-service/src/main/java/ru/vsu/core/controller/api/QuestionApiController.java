package ru.vsu.core.controller.api;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.vsu.core.model.dto.QuestionDto;
import ru.vsu.core.model.dto.QuestionFileDto;
import ru.vsu.core.model.request.QuestionCreateRequest;
import ru.vsu.core.model.request.QuestionUpdateRequest;
import ru.vsu.core.service.business.QuestionService;
import ru.vsu.core.service.storage.QuestionFileService;

import java.util.List;

@RestController
@RequestMapping("/api/question")
@AllArgsConstructor
public class QuestionApiController {
    private QuestionService questionService;
    private QuestionFileService questionFileService;

    @GetMapping("/group/{groupName}")
    public List<QuestionDto> getQuestionsByGroup(@PathVariable String groupName) {
        return questionService.findByParentGroupName(groupName);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QuestionDto saveQuestion(
            @RequestBody QuestionCreateRequest question
    ){
        return questionService.save(question);
    }

    @PatchMapping("/{questionId}")
    public QuestionDto updateQuestion(
            @PathVariable String questionId,
            @RequestBody QuestionUpdateRequest question
    ) {
        return questionService.updateTitleAndText(questionId, question);
    }

    @DeleteMapping("/{questionId}")
    public void deleteQuestion(
            @PathVariable String questionId
    ) {
        questionService.deleteById(questionId);
    }

    @GetMapping("/{questionId}/files")
    public List<QuestionFileDto> getQuestionFiles(@PathVariable String questionId) {
        List<String> fileHashes = questionService.findFileHashesByQuestionId(questionId);
        return questionFileService.getAll(fileHashes);
    }

    @PutMapping("/{questionId}/files")
    public void updateQuestionFiles(
            @PathVariable String questionId,
            @RequestBody List<String> fileHashes
    ) {
        questionService.updateFileList(questionId, fileHashes);
    }
}
