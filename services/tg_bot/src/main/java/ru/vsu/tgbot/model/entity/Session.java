package ru.vsu.tgbot.model.entity;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import ru.vsu.contract.model.response.UiMessageResponseDto;
import ru.vsu.tgbot.model.dto.GroupDto;
import ru.vsu.tgbot.util.MainMenuState;
import ru.vsu.tgbot.util.MessageState;
import ru.vsu.tgbot.util.BotState;

import java.util.ArrayList;
import java.util.List;

@RedisHash(value = "sessions", timeToLive = Session.TIME_TO_LIVE)
public class Session {
    @Value("${redis.session.timespan}")
    public static final int TIME_TO_LIVE = 120;

    @Id
    private Long chatId;
    @NotNull
    private BotState botState;
    @NotNull
    private MessageState messageState;
    @NotNull
    private MainMenuState globalState;
    @NotNull
    private List<GroupDto> groupWindow = new ArrayList<>();
    private List<UiMessageResponseDto> uiMessages;
    private Integer lastMessageId;
    private String langCode;

    public Session() {
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public BotState getBotState() {
        return botState;
    }

    public void setBotState(BotState botState) {
        this.botState = botState;
    }

    public MessageState getMessageState() {
        return messageState;
    }

    public void setMessageState(MessageState messageState) {
        this.messageState = messageState;
    }

    public MainMenuState getGlobalState() {
        return globalState;
    }

    public void setGlobalState(MainMenuState globalState) {
        this.globalState = globalState;
    }

    public List<GroupDto> getGroupWindow() {
        return groupWindow;
    }

    public void setGroupWindow(List<GroupDto> groupWindow) {
        this.groupWindow = groupWindow;
    }

    public List<UiMessageResponseDto> getUiMessages() {
        return uiMessages;
    }

    public void setUiMessages(List<UiMessageResponseDto> uiMessages) {
        this.uiMessages = uiMessages;
    }

    public Integer getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(Integer lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public String getLangCode() {
        return langCode;
    }

    public void setLangCode(String langCode) {
        this.langCode = langCode;
    }
}
