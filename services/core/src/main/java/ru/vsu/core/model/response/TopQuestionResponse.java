package ru.vsu.core.model.response;

import java.util.Map;

public record TopQuestionResponse(
        String name,
        String parent,
        Map<String, String> title,
        Map<String, String> text,
        Integer using
) {
}
