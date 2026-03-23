package ru.vsu.core.component.mapper;

import org.springframework.stereotype.Component;
import ru.vsu.core.model.dto.GroupDto;
import ru.vsu.core.model.dto.GroupTreeDto;
import ru.vsu.core.model.dto.QuestionDto;
import ru.vsu.core.model.dto.QuestionLocalizedDto;
import ru.vsu.core.model.dto.UiMessageDto;
import ru.vsu.core.model.response.GroupResponseDto;
import ru.vsu.core.model.response.LanguageResponseDto;
import ru.vsu.core.model.response.QuestionResponseDto;
import ru.vsu.core.model.response.UiMessageResponseDto;
import ru.vsu.core.model.response.UserResponseDto;

import java.util.Collections;
import java.util.Map;

@Component
public class ResponseMapper {
    public GroupResponseDto toResponse(GroupTreeDto group) {
        if (group == null) {
            return null;
        }
        return new GroupResponseDto(
                group.groupId(),
                localize(group.title()),
                group.parentId(),
                group.innerGroups() == null ? Collections.emptyList() : group.innerGroups().stream()
                        .map(this::toShallowResponse)
                        .toList(),
                group.questions() == null ? Collections.emptyList() : group.questions().stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    public GroupResponseDto toShallowResponse(GroupTreeDto group) {
        if (group == null) {
            return null;
        }
        return new GroupResponseDto(
                group.groupId(),
                localize(group.title()),
                group.parentId(),
                Collections.emptyList(),
                Collections.emptyList()
        );
    }

    public GroupResponseDto toShallowResponse(GroupDto group) {
        if (group == null) {
            return null;
        }
        return new GroupResponseDto(
                group.groupId(),
                localize(group.title()),
                group.parentId(),
                Collections.emptyList(),
                Collections.emptyList()
        );
    }

    public QuestionResponseDto toResponse(QuestionDto question) {
        if (question == null) {
            return null;
        }
        return new QuestionResponseDto(
                question.getQuestionId(),
                question.getName(),
                null,
                localize(question.getTitle()),
                localize(question.getText())
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
        return new UserResponseDto(user.getChatId(), user.getLangCode());
    }

    public UiMessageResponseDto toResponse(UiMessageDto uiMessage) {
        if (uiMessage == null) {
            return null;
        }
        return new UiMessageResponseDto(
                uiMessage.id(),
                uiMessage.name(),
                uiMessage.text()
        );
    }

    private String localize(Map<String, String> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.getOrDefault("ru", values.values().iterator().next());
    }
}
