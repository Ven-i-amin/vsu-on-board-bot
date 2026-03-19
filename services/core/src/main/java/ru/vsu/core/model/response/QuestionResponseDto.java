package ru.vsu.core.model.response;

public record QuestionResponseDto(
        String questionId,
        String name,
        GroupResponseDto parent,
        String title,
        String text
) {
}
