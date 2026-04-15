package ru.vsu.tgbot.model.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GroupDto {
    private String name;
    private Map<String, String> title;
    private String parentName;
    private List<GroupDto> innerGroups = new ArrayList<>();
    private List<QuestionDto> questions = new ArrayList<>();

    public GroupDto() {
    }

    public GroupDto(String name, Map<String, String> title, String parentName, List<GroupDto> innerGroups, List<QuestionDto> questions) {
        this.name = name;
        this.title = title;
        this.parentName = parentName;
        this.innerGroups = innerGroups;
        this.questions = questions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getTitle() {
        return title;
    }

    public void setTitle(Map<String, String> title) {
        this.title = title;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public List<GroupDto> getInnerGroups() {
        return innerGroups;
    }

    public void setInnerGroups(List<GroupDto> innerGroups) {
        this.innerGroups = innerGroups;
    }

    public List<QuestionDto> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionDto> questions) {
        this.questions = questions;
    }

    public static GroupDtoBuilder builder() {
        return new GroupDtoBuilder();
    }

    public static class GroupDtoBuilder {
        private String name;
        private Map<String, String> title;
        private String parentName;
        private List<GroupDto> innerGroups = new ArrayList<>();
        private List<QuestionDto> questions = new ArrayList<>();

        public GroupDtoBuilder name(String name) {
            this.name = name;
            return this;
        }

        public GroupDtoBuilder title(Map<String, String> title) {
            this.title = title;
            return this;
        }

        public GroupDtoBuilder parentName(String parentName) {
            this.parentName = parentName;
            return this;
        }

        public GroupDtoBuilder innerGroups(List<GroupDto> innerGroups) {
            this.innerGroups = innerGroups;
            return this;
        }

        public GroupDtoBuilder questions(List<QuestionDto> questions) {
            this.questions = questions;
            return this;
        }

        public GroupDto build() {
            return new GroupDto(name, title, parentName, innerGroups, questions);
        }
    }
}
