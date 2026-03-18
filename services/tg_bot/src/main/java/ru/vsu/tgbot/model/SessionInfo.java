package ru.vsu.tgbot.model;

import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import ru.vsu.tgbot.util.BotState;
import ru.vsu.tgbot.util.MessageState;

import java.util.List;

@Data
@Builder
public class SessionInfo {
    @NotNull
    private MessageState messageState;
    @NotNull
    private List<BotState> state;
    private List<QuestionAndAnswer> group;
    private String language;
}
