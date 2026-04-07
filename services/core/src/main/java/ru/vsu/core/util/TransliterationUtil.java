package ru.vsu.core.util;

import java.util.HashMap;
import java.util.Map;

public class TransliterationUtil {
    private static final Map<Character, String> TRANSLITERATION_MAP = createTransliterationMap();

    private TransliterationUtil() {
    }

    public static String transliterate(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        StringBuilder result = new StringBuilder();

        for (char symbol : value.toCharArray()) {
            if (Character.isWhitespace(symbol)) {
                result.append('-');
                continue;
            }

            String transliterated = TRANSLITERATION_MAP.get(Character.toLowerCase(symbol));
            if (transliterated == null) {
                result.append(symbol);
                continue;
            }

            if (Character.isUpperCase(symbol) && !transliterated.isEmpty()) {
                result.append(Character.toUpperCase(transliterated.charAt(0)));
                if (transliterated.length() > 1) {
                    result.append(transliterated.substring(1));
                }
                continue;
            }

            result.append(transliterated);
        }

        return result.toString();
    }

    private static Map<Character, String> createTransliterationMap() {
        Map<Character, String> map = new HashMap<>();
        map.put('а', "a");
        map.put('б', "b");
        map.put('в', "v");
        map.put('г', "g");
        map.put('д', "d");
        map.put('е', "e");
        map.put('ё', "yo");
        map.put('ж', "zh");
        map.put('з', "z");
        map.put('и', "i");
        map.put('й', "y");
        map.put('к', "k");
        map.put('л', "l");
        map.put('м', "m");
        map.put('н', "n");
        map.put('о', "o");
        map.put('п', "p");
        map.put('р', "r");
        map.put('с', "s");
        map.put('т', "t");
        map.put('у', "u");
        map.put('ф', "f");
        map.put('х', "kh");
        map.put('ц', "ts");
        map.put('ч', "ch");
        map.put('ш', "sh");
        map.put('щ', "shch");
        map.put('ъ', "");
        map.put('ы', "y");
        map.put('ь', "");
        map.put('э', "e");
        map.put('ю', "yu");
        map.put('я', "ya");
        return map;
    }
}
