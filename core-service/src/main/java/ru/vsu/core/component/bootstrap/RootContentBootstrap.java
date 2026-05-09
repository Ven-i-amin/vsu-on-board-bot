package ru.vsu.core.component.bootstrap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.vsu.core.model.dto.GroupDto;
import ru.vsu.core.model.dto.QuestionDto;
import ru.vsu.core.service.GroupService;
import ru.vsu.core.service.QuestionService;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
@Order(40)
@RequiredArgsConstructor
@ConditionalOnProperty(
        value = "app.bootstrap.root-content.enabled",
        havingValue = "true"
)
public class RootContentBootstrap implements ApplicationRunner {
    private final GroupService groupService;
    private final QuestionService questionService;
    private final StartValueResourceLoader startValueResourceLoader;

    @Override
    public void run(ApplicationArguments args) {
        GroupDto rootGroup = groupService.createRootIfMissing();

        createMissingGroups(rootGroup);
        createMissingQuestions();
    }

    private void createMissingGroups(GroupDto rootGroup) {
        Map<String, StartValueResourceLoader.GroupStartValue> pendingGroups = new LinkedHashMap<>();
        for (StartValueResourceLoader.GroupStartValue group : startValueResourceLoader.loadGroups()) {
            pendingGroups.put(group.name(), group);
        }

        boolean progress = true;
        while (!pendingGroups.isEmpty() && progress) {
            progress = false;

            var iterator = pendingGroups.entrySet().iterator();
            while (iterator.hasNext()) {
                StartValueResourceLoader.GroupStartValue startGroup = iterator.next().getValue();

                if (groupService.findByName(startGroup.name()) != null) {
                    iterator.remove();
                    progress = true;
                    continue;
                }

                String parentName = resolveParentName(startGroup.parentName(), rootGroup.name());
                if (parentName != null && groupService.findByName(parentName) == null) {
                    continue;
                }

                groupService.save(GroupDto.builder()
                        .name(startGroup.name())
                        .title(startGroup.title())
                        .parentName(parentName)
                        .build());
                iterator.remove();
                progress = true;
            }
        }

        if (!pendingGroups.isEmpty()) {
            log.warn("Failed to create some start groups because parent groups were not found: {}", pendingGroups.keySet());
        }
    }

    private void createMissingQuestions() {
        for (StartValueResourceLoader.QuestionStartValue question : startValueResourceLoader.loadQuestions()) {
            if (questionService.findByParentGroupNameAndName(question.groupName(), question.name()) != null) {
                continue;
            }

            if (groupService.findByName(question.groupName()) == null) {
                log.warn("Skipping start question '{}' because parent group '{}' does not exist", question.name(), question.groupName());
                continue;
            }

            questionService.save(QuestionDto.builder()
                    .name(question.name())
                    .parent(question.groupName())
                    .title(question.title())
                    .text(question.text())
                    .build());
        }
    }

    private String resolveParentName(String parentName, String rootGroupName) {
        if (startValueResourceLoader.isRootParentAlias(parentName)) {
            return rootGroupName;
        }

        return parentName;
    }
}
