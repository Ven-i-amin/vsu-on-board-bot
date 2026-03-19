package ru.vsu.tgbot.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import ru.vsu.tgbot.model.dto.GroupDto;
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
    public static final int TIME_TO_LIVE = 3600;
    @Id
    private Long chatId;
    @NotNull
    private BotState botState;
    @NotNull
    private MessageState messageState;
    private GroupDto start;
    @NotNull
    private List<GroupDto> groupWindow = new ArrayList<>();
    private String language;
}
