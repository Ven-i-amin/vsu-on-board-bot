package ru.vsu.tgbot.model.dto;

import jakarta.validation.constraints.NotEmpty;
import org.checkerframework.common.aliasing.qual.Unique;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class UiMessageDto {
    @NotNull
    @Unique
    private String name;
    @NotNull
    @NotEmpty
    private Map<String, String> text;

    public UiMessageDto() {
    }

    public UiMessageDto(String name, Map<String, String> text) {
        this.name = name;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getText() {
        return text;
    }

    public void setText(Map<String, String> text) {
        this.text = text;
    }

    public static UiMessageDtoBuilder builder() {
        return new UiMessageDtoBuilder();
    }

    public static class UiMessageDtoBuilder {
        private String name;
        private Map<String, String> text;

        public UiMessageDtoBuilder name(String name) {
            this.name = name;
            return this;
        }

        public UiMessageDtoBuilder text(Map<String, String> text) {
            this.text = text;
            return this;
        }

        public UiMessageDto build() {
            return new UiMessageDto(name, text);
        }
    }
}
