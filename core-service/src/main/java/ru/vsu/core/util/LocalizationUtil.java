package ru.vsu.core.util;

import java.util.Map;

public class LocalizationUtil {
    public static final String DEFAULT_LANGUAGE_CODE = "ru";

    public static String localize(Map<String, String> values, String languageCode) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        if (languageCode != null && values.containsKey(languageCode)) {
            return values.get(languageCode);
        }
        return values.getOrDefault(DEFAULT_LANGUAGE_CODE, values.values().iterator().next());
    }

    public static boolean hasDefaultLanguage(Map<String, String> values) {
        if (values == null || values.isEmpty()) {
            return false;
        }

        return values.containsKey(DEFAULT_LANGUAGE_CODE);
    }
}
