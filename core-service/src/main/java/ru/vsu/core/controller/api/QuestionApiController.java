package ru.vsu.core.controller.api;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.vsu.core.model.dto.QuestionDto;
import ru.vsu.core.model.request.QuestionCreateRequest;
import ru.vsu.core.model.request.QuestionUpdateRequest;
import ru.vsu.core.service.QuestionService;

@RestController
@RequestMapping("/api/question")
@AllArgsConstructor
public class QuestionApiController {
    private QuestionService questionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void saveQuestion(
            @RequestBody QuestionCreateRequest question
    ){
        questionService.save(question);
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
}
