package ru.vsu.core.component.bootstrap;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.vsu.core.model.dto.GroupDto;
import ru.vsu.core.model.dto.QuestionDto;
import ru.vsu.core.service.GroupService;
import ru.vsu.core.service.QuestionService;

import java.util.List;
import java.util.Map;

@Component
@Order(40)
@RequiredArgsConstructor
@ConditionalOnProperty(
        value = "app.bootstrap.root-content.enabled",
        havingValue = "true"
)
public class RootContentBootstrap implements ApplicationRunner {
    private static final String DEFAULT_LANGUAGE_CODE = "ru";

    private final GroupService groupService;
    private final QuestionService questionService;

    @Override
    public void run(ApplicationArguments args) {
        GroupDto rootGroup = groupService.createRootIfMissing();

        GroupDto aboutGroup = ensureGroup(rootGroup.groupId(), "about-service", "О сервисе");
        GroupDto languageGroup = ensureGroup(rootGroup.groupId(), "language-settings", "Настройки языка");

        ensureQuestion(
                aboutGroup.name(),
                "what-can-bot-do",
                "Что умеет бот?",
                "Бот помогает выбрать раздел и получить ответы на часто задаваемые вопросы."
        );
        ensureQuestion(
                aboutGroup.name(),
                "how-to-start",
                "С чего начать?",
                "Откройте нужный раздел в главном меню и выберите интересующий вопрос."
        );
        ensureQuestion(
                languageGroup.name(),
                "how-to-change-language",
                "Как сменить язык?",
                "Нажмите кнопку «Выбрать язык» в главном меню и выберите русский."
        );
        ensureQuestion(
                languageGroup.name(),
                "available-languages",
                "Какие языки доступны?",
                "По умолчанию сервис создаёт только русский язык."
        );
    }

    private GroupDto ensureGroup(String parentGroupId, String name, String title) {
        GroupDto existingGroup = groupService.findByName(name);
        if (existingGroup != null) {
            return existingGroup;
        }

        GroupDto parentGroup = groupService.findById(parentGroupId);
        String parentName = parentGroup == null ? null : parentGroup.name();

        return groupService.save(GroupDto.builder()
                .name(name)
                .title(localizedValue(title))
                .parentName(parentName)
                .build());
    }

    private void ensureQuestion(String groupName, String name, String title, String text) {
        if (questionService.findByParentGroupNameAndName(groupName, name) != null) {
            return;
        }

        questionService.save(QuestionDto.builder()
                .name(name)
                .parent(groupName)
                .title(localizedValue(title))
                .text(localizedValue(text))
                .build());
    }

    private Map<String, String> localizedValue(String value) {
        return Map.of(DEFAULT_LANGUAGE_CODE, value);
    }
}
