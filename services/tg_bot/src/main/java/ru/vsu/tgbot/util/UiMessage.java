package ru.vsu.tgbot.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UiMessage {
    BACK("back"),
    START("start"),
    WELCOME("welcome"),
    QUESTION_LISTEN("question_listen"),
    QUESTION_ANSWER("question_answer"),;

    private final String value;
}
