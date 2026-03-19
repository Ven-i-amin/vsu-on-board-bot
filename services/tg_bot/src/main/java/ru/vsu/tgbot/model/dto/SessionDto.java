package ru.vsu.tgbot.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import ru.vsu.tgbot.model.response.GroupResponseDto;
import ru.vsu.tgbot.util.MessageState;
import ru.vsu.tgbot.util.BotState;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionDto {
    @Id
    private Long chatId;
    private String text;
    @NotNull
    private BotState botState;
    @NotNull
    private MessageState messageState;
    private GroupResponseDto start;
    private List<GroupResponseDto> groupWindow;
    private String language;
}
