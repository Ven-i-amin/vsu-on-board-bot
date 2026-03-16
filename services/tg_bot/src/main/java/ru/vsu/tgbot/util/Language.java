package ru.vsu.tgbot.util;

public enum Language {
    RU("Русский"),
    EN("English");

    private final String value;

    Language(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
