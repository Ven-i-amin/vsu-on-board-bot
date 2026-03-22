package ru.vsu.core.util;

import java.util.Map;

public class LocalizationUtil {
    public static String localize(Map<String, String> values, String languageCode) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        if (languageCode != null && values.containsKey(languageCode)) {
            return values.get(languageCode);
        }
        return values.getOrDefault("ru", values.values().iterator().next());
    }
}
