package ru.vsu.core.controller.api;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vsu.core.model.response.LanguageCountResponse;
import ru.vsu.core.model.response.TopQuestionResponse;
import ru.vsu.core.service.business.QuestionService;
import ru.vsu.core.service.user.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/statistic")
@AllArgsConstructor
public class StatisticApiController {
    private final UserService userService;
    private final QuestionService questionService;

    @GetMapping("/topLanguages")
    public List<LanguageCountResponse> topLanguages() {
        return userService.getUserLanguageUsage();
    }

    @GetMapping("/topQuestions")
    public List<TopQuestionResponse> topQuestions() {
        return questionService.findTopQuestions();
    }
}
