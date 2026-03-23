package ru.vsu.tgbot.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.vsu.tgbot.model.dto.GroupDto;
import ru.vsu.tgbot.model.dto.SessionDto;

import java.util.ArrayList;
import java.util.List;

public class MessageUtil {
    public static String NOT_FOUND_MESSAGE = "Not Found";

    public static GroupDto getCurrentGroup(List<GroupDto> group) {
        return group.getLast();
    }

    public static GroupDto getGroupByText(String userText, SessionDto sessionDto) {
        return sessionDto.getStart().innerGroups().stream()
                .filter(el ->
                        MessageUtil.isLocalizedGroupTitle(userText, el, sessionDto)
                )
                .findFirst()
                .orElse(null);
    }

    public static Boolean isLocalizedGroupTitle(String text, GroupDto groupDto, SessionDto sessionDto) {
        return groupDto.title().get(sessionDto.getLangCode()).equals(text);
    }

    public static List<String> getLocalizedGroupTitles(SessionDto sessionDto) {
        return sessionDto.getStart().innerGroups().stream()
                .map(GroupDto::title)
                .map(el -> el.get(sessionDto.getLangCode()))
                .toList();
    }

    public static List<InlineKeyboardRow> getInlineButtonColumn(List<String> texts, int rowSize) {
        assert rowSize > 0;

        List<InlineKeyboardRow> keyboardRows = new ArrayList<>();

        int rowFill = 0;
        InlineKeyboardRow row = new InlineKeyboardRow();

        for (String text : texts) {
            if (rowFill == rowSize) {
                rowFill = 0;

                keyboardRows.add(row);
                row = new InlineKeyboardRow();
            }

            InlineKeyboardButton button = new InlineKeyboardButton(text);

            row.add(button);

            rowFill++;
        }

        keyboardRows.add(row);

        return keyboardRows;
    }

    public static List<KeyboardRow> getButtonColumn(List<String> texts, int rowSize) {
        assert rowSize > 0;

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        int rowFill = 0;
        KeyboardRow row = new KeyboardRow();

        for (String text : texts) {
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
}
