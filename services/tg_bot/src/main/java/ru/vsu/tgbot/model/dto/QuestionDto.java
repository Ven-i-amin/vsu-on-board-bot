package ru.vsu.tgbot.model.dto;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class QuestionDto {
    private String questionId;
    private String name;
    private String parent;
    @NotNull
    private Map<String, String> title = new HashMap<>();
    @NotNull
    private Map<String, String> text = new HashMap<>();

    public QuestionDto() {
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public Map<String, String> getTitle() {
        return title;
    }

    public void setTitle(Map<String, String> title) {
        this.title = title;
    }

    public Map<String, String> getText() {
        return text;
    }

    public void setText(Map<String, String> text) {
        this.text = text;
    }

    public static QuestionDtoBuilder builder() {
        return new QuestionDtoBuilder();
    }

    public static class QuestionDtoBuilder {
        private String questionId;
        private String name;
        private String parent;
        private Map<String, String> title = new HashMap<>();
        private Map<String, String> text = new HashMap<>();

        public QuestionDtoBuilder questionId(String questionId) {
            this.questionId = questionId;
            return this;
        }

        public QuestionDtoBuilder name(String name) {
            this.name = name;
            return this;
        }

        public QuestionDtoBuilder parent(String parent) {
            this.parent = parent;
            return this;
        }

        public QuestionDtoBuilder title(Map<String, String> title) {
            this.title = title;
            return this;
        }

        public QuestionDtoBuilder text(Map<String, String> text) {
            this.text = text;
            return this;
        }

        public QuestionDto build() {
            QuestionDto dto = new QuestionDto();
            dto.setQuestionId(questionId);
            dto.setName(name);
            dto.setParent(parent);
            dto.setTitle(title);
            dto.setText(text);
            return dto;
        }
    }
}
