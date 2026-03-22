package ru.vsu.tgbot.model.response;

import java.util.Map;

public record UiMessageResponse(
        String id,
        String name,
        Map<String, String> text
) {
}
