package ru.vsu.tgbot.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.model.response.QuestionResponseDto;

public class MessageUtil {
    public static InlineKeyboardRow getBackButtonRow(SessionDto session) {
        QuestionResponseDto backQuestion = GroupUtil.getSpecialQuestion(session, "back");
        InlineKeyboardButton button = new InlineKeyboardButton(backQuestion.getText());

        return new InlineKeyboardRow(button);
    }

    public static InlineKeyboardRow getToStartButtonRow(SessionDto session) {
        QuestionResponseDto toStartQuestion = GroupUtil.getSpecialQuestion(session, "toStart");
        InlineKeyboardButton button = new InlineKeyboardButton(toStartQuestion.getText());

        return new InlineKeyboardRow(button);
    }

    public static boolean isBackButton(SessionDto session) {
        return text.equals(GroupUtil.getSpecialQuestion(session, "back"));
    }

    public static boolean isToStartButton(SessionDto session) {
        return text.equals(GroupUtil.getSpecialQuestion(session, "toStart"));
    }
}
