package ru.vsu.tgbot.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupDto {
    private String name;
    private Map<String, String> title;
    private String parentName;
    @Builder.Default
    private List<GroupDto> innerGroups = new ArrayList<>();
    @Builder.Default
    private List<QuestionDto> questions = new ArrayList<>();
}
