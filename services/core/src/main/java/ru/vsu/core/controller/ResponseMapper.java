package ru.vsu.core.controller;

import org.springframework.stereotype.Component;
import ru.vsu.core.model.dto.GroupLocalizedDto;
import ru.vsu.core.model.dto.QuestionLocalizedDto;
import ru.vsu.core.model.response.GroupResponseDto;
import ru.vsu.core.model.response.LanguageResponseDto;
import ru.vsu.core.model.response.QuestionResponseDto;
import ru.vsu.core.model.response.UserResponseDto;

import java.util.Collections;

@Component
public class ResponseMapper {
    public GroupResponseDto toResponse(GroupLocalizedDto group) {
        if (group == null) {
            return null;
        }
        return new GroupResponseDto(
                group.groupId(),
                group.title(),
                group.parent() == null ? null : group.parent().groupId(),
                group.innerGroups() == null ? Collections.emptyList() : group.innerGroups().stream()
                        .map(this::toShallowResponse)
                        .toList(),
                group.questions() == null ? Collections.emptyList() : group.questions().stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    public GroupResponseDto toShallowResponse(GroupLocalizedDto group) {
        if (group == null) {
            return null;
        }
        return new GroupResponseDto(
                group.groupId(),
                group.title(),
                group.parent() == null ? null : group.parent().groupId(),
                Collections.emptyList(),
                Collections.emptyList()
        );
    }

    public QuestionResponseDto toResponse(QuestionLocalizedDto question) {
        if (question == null) {
            return null;
        }
        return new QuestionResponseDto(
                question.getQuestionId(),
                question.getName(),
                toShallowResponse(question.getParent()),
                question.getTitle(),
                question.getText()
        );
    }

    public LanguageResponseDto toResponse(ru.vsu.core.model.dto.LanguageDto language) {
        return new LanguageResponseDto(language.code(), language.name());
    }

    public UserResponseDto toResponse(ru.vsu.core.model.dto.UserDto user) {
        return new UserResponseDto(user.getChatId(), user.getLanguageCode());
    }
}
