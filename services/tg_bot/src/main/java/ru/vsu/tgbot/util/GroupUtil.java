package ru.vsu.tgbot.util;

import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.model.response.GroupResponseDto;
import ru.vsu.tgbot.model.response.QuestionResponseDto;

import java.util.List;

public class GroupUtil {
    public static String NOT_FOUND_MESSAGE = "Not Found";

    public static QuestionResponseDto getSpecialQuestion(SessionDto sessionDto, String questionName) {
        return sessionDto.getStart().questions().stream()
                .filter(quest -> quest.getName()
                        .equals(questionName))
                .findFirst()
                .orElse(QuestionResponseDto.builder().text(NOT_FOUND_MESSAGE).build());
    }

    public static GroupResponseDto getCurrentGroup(List<GroupResponseDto> group) {
        return group.get(group.size() / 2);
    }
}
