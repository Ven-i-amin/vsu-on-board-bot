package ru.vsu.tgbot.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.vsu.tgbot.model.dto.GroupDto;
import ru.vsu.tgbot.model.dto.LanguageDto;

import java.util.ArrayList;
import java.util.List;

public class StateHandlerUtil {
    public static String NOT_FOUND_MESSAGE = "Not Found";

    public static GroupDto getCurrentGroup(List<GroupDto> group) {
        return group.getLast();
    }

    public static List<InlineKeyboardRow> getButtonColumn(List<String> texts, int rowSize) {
        List<InlineKeyboardRow> keyboardRows = new ArrayList<>();

        int rowFill = 0;
        InlineKeyboardRow row = new InlineKeyboardRow();

        for (String text : texts) {
            if (rowFill == rowSize) {
                rowFill = 0;
                row = new InlineKeyboardRow();
            }

            InlineKeyboardButton button = new InlineKeyboardButton(text);

            row.add(button);
            keyboardRows.add(row);

            rowFill++;
        }

        return keyboardRows;
    }
}
