package ru.vsu.tgbot.model.dto;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vsu.contract.model.response.UiMessageResponseDto;
import ru.vsu.tgbot.util.MainMenuState;
import ru.vsu.tgbot.util.MessageState;
import ru.vsu.tgbot.util.BotState;

import java.util.ArrayList;
import java.util.List;

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
    private GroupDto start;
    @NotNull
    private List<GroupDto> groupWindow = new ArrayList<>();
    private List<UiMessageResponseDto> uiMessages;
    private Integer lastMessageId;
    private String langCode;

    public SessionDto() {
    }

    public SessionDto(
            Long chatId,
            Update update,
            BotState botState,
            MessageState messageState,
            MainMenuState globalState,
            GroupDto start,
            List<GroupDto> groupWindow,
            List<UiMessageResponseDto> uiMessages,
            Integer lastMessageId,
            String langCode
    ) {
        this.chatId = chatId;
        this.update = update;
        this.botState = botState;
        this.messageState = messageState;
        this.globalState = globalState;
        this.start = start;
        this.groupWindow = groupWindow;
        this.uiMessages = uiMessages;
        this.lastMessageId = lastMessageId;
        this.langCode = langCode;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Update getUpdate() {
        return update;
    }

    public void setUpdate(Update update) {
        this.update = update;
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

    public GroupDto getStart() {
        return start;
    }

    public void setStart(GroupDto start) {
        this.start = start;
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

    public static SessionDtoBuilder builder() {
        return new SessionDtoBuilder();
    }

    public static class SessionDtoBuilder {
        private Long chatId;
        private Update update;
        private BotState botState;
        private MessageState messageState;
        private MainMenuState globalState;
        private GroupDto start;
        private List<GroupDto> groupWindow = new ArrayList<>();
        private List<UiMessageResponseDto> uiMessages;
        private Integer lastMessageId;
        private String langCode;

        public SessionDtoBuilder chatId(Long chatId) {
            this.chatId = chatId;
            return this;
        }

        public SessionDtoBuilder update(Update update) {
            this.update = update;
            return this;
        }

        public SessionDtoBuilder botState(BotState botState) {
            this.botState = botState;
            return this;
        }

        public SessionDtoBuilder messageState(MessageState messageState) {
            this.messageState = messageState;
            return this;
        }

        public SessionDtoBuilder globalState(MainMenuState globalState) {
            this.globalState = globalState;
            return this;
        }

        public SessionDtoBuilder start(GroupDto start) {
            this.start = start;
            return this;
        }

        public SessionDtoBuilder groupWindow(List<GroupDto> groupWindow) {
            this.groupWindow = groupWindow;
            return this;
        }

        public SessionDtoBuilder uiMessages(List<UiMessageResponseDto> uiMessages) {
            this.uiMessages = uiMessages;
            return this;
        }

        public SessionDtoBuilder lastMessageId(Integer lastMessageId) {
            this.lastMessageId = lastMessageId;
            return this;
        }

        public SessionDtoBuilder langCode(String langCode) {
            this.langCode = langCode;
            return this;
        }

        public SessionDto build() {
            return new SessionDto(chatId, update, botState, messageState, globalState, start, groupWindow, uiMessages, lastMessageId, langCode);
        }
    }

    @Override
    public String toString() {
        return "SessionDto{" +
                "chatId=" + chatId +
                ", botState=" + botState +
                ", messageState=" + messageState +
                ", globalState=" + globalState +
                ", lastMessageId=" + lastMessageId +
                ", langCode='" + langCode + '\'' +
                '}';
    }
}
