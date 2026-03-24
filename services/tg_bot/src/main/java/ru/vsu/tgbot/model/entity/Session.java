package ru.vsu.tgbot.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.vsu.contract.model.response.UiMessageResponseDto;
import ru.vsu.tgbot.model.dto.GroupDto;
import ru.vsu.tgbot.util.GlobalState;
import ru.vsu.tgbot.util.MessageState;
import ru.vsu.tgbot.util.BotState;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "session", timeToLive = Session.TIME_TO_LIVE)
public class Session {
    public static final int TIME_TO_LIVE = 120;
    @Id
    private Long chatId;
    @NotNull
    private BotState botState;
    @NotNull
    private MessageState messageState;
    @NotNull
    private GlobalState globalState;
    private GroupDto start;
    @NotNull
    @Builder.Default
    private List<GroupDto> groupWindow = new ArrayList<>();
    private List<UiMessageResponseDto> uiMessages;
    private Integer lastMessageId;
    private String langCode;
}
