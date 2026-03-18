package ru.vsu.tgbot.model.dto;

import lombok.Builder;
import ru.vsu.tgbot.model.entity.QuestionAndAnswer;
import ru.vsu.tgbot.util.BotState;
import ru.vsu.tgbot.util.MessageState;

import java.util.List;

@Builder
public record SessionDto(
        Long chatId,
        String text,
        MessageState messageState,
        List<BotState> state,
        List<QuestionAndAnswer> questionAndAnswer,
        String language
) {
}
