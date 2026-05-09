package ru.vsu.tgbot.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UiMessageName {
    BACK("back"),
    START("start"),
    ERROR("error"),
    WELCOME("welcome"),
    MAIN_MENU("main-menu"),
    LANGUAGE_TITLE("language-title"),
    QUESTION_LISTEN("question-listen"),
    QUESTION_ANSWER("question-answer"),
    OTHER_LANGUAGE_MENU("other-language-menu");

    private final String value;
}
