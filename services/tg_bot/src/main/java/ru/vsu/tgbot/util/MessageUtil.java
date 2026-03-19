package ru.vsu.tgbot.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.vsu.tgbot.model.dto.QuestionDto;
import ru.vsu.tgbot.model.dto.SessionDto;

public class MessageUtil {
    public static InlineKeyboardRow getBackButtonRow(SessionDto session) {
        QuestionDto backQuestion = GroupUtil.getSpecialQuestion(session, "back");
        InlineKeyboardButton button = new InlineKeyboardButton(backQuestion.getText());

        return new InlineKeyboardRow(button);
    }

    public static InlineKeyboardRow getToStartButtonRow(SessionDto session) {
        QuestionDto toStartQuestion = GroupUtil.getSpecialQuestion(session, "toStart");
        InlineKeyboardButton button = new InlineKeyboardButton(toStartQuestion.getText());

        return new InlineKeyboardRow(button);
    }

    public static boolean isBackButton(SessionDto session) {
        QuestionDto backQuestion = GroupUtil.getSpecialQuestion(session, "back");
        return backQuestion.getText().equals(session.getText());
    }

    public static boolean isToStartButton(SessionDto session) {
        QuestionDto toStartQuestion = GroupUtil.getSpecialQuestion(session, "toStart");
        return toStartQuestion.getText().equals(session.getText());
    }
}
