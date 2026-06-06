package ru.vsu.core.model.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record QuestionUpdateRequest(
        @NotNull @NotEmpty Map<String, String> title,
        @NotNull @NotEmpty Map<String, String> text
) {

}
