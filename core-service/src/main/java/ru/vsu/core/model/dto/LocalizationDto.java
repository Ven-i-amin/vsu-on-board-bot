package ru.vsu.core.model.dto;

import lombok.Builder;

@Builder
public class LocalizationDto {
    private String id;
    private String title;
    private String text;
    private String langCode;
}
