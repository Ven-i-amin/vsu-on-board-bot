package ru.vsu.core.controller.bot;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vsu.core.service.QuestionService;

@RestController
@RequestMapping("/bot/question")
@AllArgsConstructor
public class QuestionController {
    private final QuestionService questionService;

    @PostMapping("/{questionName}/fixate")
    public void fixate(@PathVariable("questionName") String questionName){

    }
}
