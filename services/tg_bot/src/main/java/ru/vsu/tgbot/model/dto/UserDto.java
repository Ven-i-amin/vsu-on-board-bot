package ru.vsu.tgbot.model.dto;

public class UserDto {
    private Long chatId;
    private String langCode;

    public UserDto() {
    }

    public UserDto(Long chatId, String langCode) {
        this.chatId = chatId;
        this.langCode = langCode;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getLangCode() {
        return langCode;
    }

    public void setLangCode(String langCode) {
        this.langCode = langCode;
    }

    public static UserDtoBuilder builder() {
        return new UserDtoBuilder();
    }

    public static class UserDtoBuilder {
        private Long chatId;
        private String langCode;

        public UserDtoBuilder chatId(Long chatId) {
            this.chatId = chatId;
            return this;
        }

        public UserDtoBuilder langCode(String langCode) {
            this.langCode = langCode;
            return this;
        }

        public UserDto build() {
            return new UserDto(chatId, langCode);
        }
    }
}
