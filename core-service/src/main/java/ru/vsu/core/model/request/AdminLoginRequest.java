package ru.vsu.core.model.request;

import jakarta.validation.constraints.NotBlank;

public record AdminLoginRequest(
        @NotBlank String email,
        @NotBlank String password
) {
}
