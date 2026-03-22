package ru.vsu.core.model.dto;

public record LocalizedQuestionNodeDto(
        String questionId,
        String name,
        String parent,
        String title,
        String text
) {
}
