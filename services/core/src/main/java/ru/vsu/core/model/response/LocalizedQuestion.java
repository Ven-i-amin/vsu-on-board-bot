package ru.vsu.core.model.response;

public record LocalizedQuestion(
        String questionId,
        String name,
        String groupId,
        String title,
        String text
) {
}