package ru.vsu.core.model.response;

import java.util.Map;

public record LanguageCountResponse(
        String languageCode,
        Map<String, String> name,
        Integer count
){
}
