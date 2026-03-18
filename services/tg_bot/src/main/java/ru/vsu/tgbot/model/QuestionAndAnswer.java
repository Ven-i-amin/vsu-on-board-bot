package ru.vsu.tgbot.model;

import lombok.Data;

import java.util.List;

@Data
public class QuestionAndAnswer {
    private String question;
    private List<QuestionAndAnswer> group;
    private String answer;
}
