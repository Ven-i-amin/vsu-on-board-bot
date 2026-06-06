package ru.vsu.contract.model.response;

import java.util.Map;

public record QuestionResponseDto(
        String questionId,
        String name,
        String parent,
        Map<String, String> title,
        Map<String, String> text
) {
}
