package ru.vsu.tgbot.model.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vsu.contract.model.response.UiMessageResponseDto;
import ru.vsu.tgbot.util.MessageState;
import ru.vsu.tgbot.util.BotState;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionDto {
    @Id
    private Long chatId;
    private Update update;
    @NotNull
    private BotState botState;
    @NotNull
    private MessageState messageState;
    private GroupDto start;
    @Builder.Default
    private List<GroupDto> groupWindow = new ArrayList<>();
    private List<UiMessageResponseDto> uiMessages;
    private String langCode;
}
