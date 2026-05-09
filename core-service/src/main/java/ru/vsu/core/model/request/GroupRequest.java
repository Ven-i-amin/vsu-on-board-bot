package ru.vsu.core.model.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record GroupRequest (
    @NotNull @NotEmpty Map<String, String> title,
    @NotNull String parentName
) {}
