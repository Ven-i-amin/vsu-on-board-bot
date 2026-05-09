package ru.vsu.core.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminRegisterRequest(
        @NotBlank String email,
        @NotBlank @Size(min = 6) String password
) {
}
