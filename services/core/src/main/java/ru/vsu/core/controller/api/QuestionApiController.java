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
@CrossOrigin(origins = "http://localhost:5173")
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

    @PatchMapping("/{questionName}")
    public QuestionDto updateQuestion(
            @PathVariable String questionName,
            @RequestBody QuestionUpdateRequest question
    ) {
        return questionService.updateTitleAndText(questionName, question);
    }

    @DeleteMapping("/{questionName}")
    public void deleteQuestion(
            @PathVariable String questionName
    ) {
        questionService.deleteByName(questionName);
    }
}
