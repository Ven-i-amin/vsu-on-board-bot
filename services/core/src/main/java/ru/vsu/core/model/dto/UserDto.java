package ru.vsu.core.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private Long chatId;
    private String langCode;
}
