package ru.vsu.core.model.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record UiMessageUpdateRequest(
        @NotNull @NotEmpty Map<String, String> text
) {

}
