package ru.vsu.tgbot.model.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import org.checkerframework.common.aliasing.qual.Unique;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@Builder
@Data
public class UiMessageDto {
    @NotNull
    @Unique
    private String name;
    @NotNull
    @NotEmpty
    private Map<String, String> text;
}
