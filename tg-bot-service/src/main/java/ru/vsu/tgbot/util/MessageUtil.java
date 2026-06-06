package ru.vsu.tgbot.util;

import org.apache.commons.lang3.tuple.Pair;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.vsu.tgbot.model.dto.GroupDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class MessageUtil {
    public static String DEFAULT_LANGUAGE_CODE = "ru";
    public static String NOT_FOUND_MESSAGE = "Not Found";

    public static String extractUserInput(Update update) {
        if (update == null) return null;

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            return callbackQuery == null ? null : callbackQuery.getData();
        }

        if (update.hasMessage() && update.getMessage().hasText()) {
            return update.getMessage().getText();
        }

        return null;
    }

    public static Long extractChatId(Update update) {
        if (update == null) return null;

        if (update.hasCallbackQuery()
                && update.getCallbackQuery() != null
                && update.getCallbackQuery().getMessage() != null) {
            return update.getCallbackQuery().getMessage().getChatId();
        }

        if (update.hasMessage() && update.getMessage() != null) {
            return update.getMessage().getChatId();
        }

        return null;
    }

    public static GroupDto getGroupByText(String userText, GroupDto parentGroup, String langCode) {
        if (parentGroup == null) return null;
        return parentGroup.getInnerGroups().stream()
                .filter(group -> Objects.equals(getLocalizedValue(group.getTitle(), langCode), userText))
                .findFirst()
                .orElse(null);
    }

    public static List<String> getLocalizedGroupTitles(GroupDto group, String langCode) {
        if (group == null || group.getInnerGroups() == null) return List.of();
        return group.getInnerGroups().stream()
                .map(g -> getLocalizedValue(g.getTitle(), langCode))
                .filter(Objects::nonNull)
                .toList();
    }

    public static List<InlineKeyboardRow> createInlineButtonColumn(List<Pair<String, String>> namesAndTexts, int rowSize) {
        assert rowSize > 0;
        List<InlineKeyboardRow> keyboardRows = new ArrayList<>();
        int rowFill = 0;
        InlineKeyboardRow row = new InlineKeyboardRow();

        for (Pair<String, String> nameAndText : namesAndTexts) {
            if (nameAndText == null || nameAndText.getRight() == null || nameAndText.getLeft() == null) {
                continue;
            }

            if (rowFill == rowSize) {
                rowFill = 0;
                keyboardRows.add(row);
                row = new InlineKeyboardRow();
            }

            InlineKeyboardButton button = new InlineKeyboardButton(nameAndText.getRight());
            button.setCallbackData(nameAndText.getLeft());
            row.add(button);
            rowFill++;
        }

        keyboardRows.add(row);
        return keyboardRows;
    }

    public static List<KeyboardRow> createButtonColumn(List<String> texts, int rowSize) {
        assert rowSize > 0;
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        int rowFill = 0;
        KeyboardRow row = new KeyboardRow();

        for (String text : texts) {
            if (text == null) continue;

            if (rowFill == rowSize) {
                rowFill = 0;
                keyboardRows.add(row);
                row = new KeyboardRow();
            }

            KeyboardButton button = new KeyboardButton(text);
            row.add(button);
            rowFill++;
        }

        keyboardRows.add(row);
        return keyboardRows;
    }

    private static String getLocalizedValue(Map<String, String> localizedValues, String langCode) {
        if (localizedValues == null || localizedValues.isEmpty()) return null;
        return Stream.of(langCode, DEFAULT_LANGUAGE_CODE)
                .filter(Objects::nonNull)
                .map(localizedValues::get)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
}
