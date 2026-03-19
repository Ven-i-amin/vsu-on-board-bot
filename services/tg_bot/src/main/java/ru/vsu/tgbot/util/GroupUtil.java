package ru.vsu.tgbot.util;

import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.model.dto.GroupDto;
import ru.vsu.tgbot.model.dto.QuestionDto;

import java.util.List;

public class GroupUtil {
    public static String NOT_FOUND_MESSAGE = "Not Found";

    public static QuestionDto getSpecialQuestion(SessionDto sessionDto, String questionName) {
        return sessionDto.getStart().questions().stream()
                .filter(quest -> quest.getName()
                        .equals(questionName))
                .findFirst()
                .orElse(QuestionDto.builder().text(NOT_FOUND_MESSAGE).build());
    }

    public static GroupDto getCurrentGroup(List<GroupDto> group) {
        return group.get(group.size() / 2);
    }
}
