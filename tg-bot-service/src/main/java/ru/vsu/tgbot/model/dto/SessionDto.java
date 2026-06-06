package ru.vsu.tgbot.model.dto;

import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vsu.contract.model.response.UiMessageResponseDto;
import ru.vsu.tgbot.util.BotState;
import ru.vsu.tgbot.util.MainMenuState;
import ru.vsu.tgbot.util.MessageState;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SessionDto {
    @Id
    private Long chatId;
    private Update update;
    @NotNull
    private BotState botState;
    @NotNull
    private MessageState messageState;
    @NotNull
    private MainMenuState globalState;
    private List<UiMessageResponseDto> uiMessages;
    private Integer lastMessageId;
    private String langCode;
}
