package ru.vsu.core.service.business;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.core.model.dto.LocalizationDto;
import ru.vsu.core.service.GroupService;
import ru.vsu.core.service.QuestionService;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class LocalizationServiceImpl implements LocalizationService{
    private GroupService groupService;
    private QuestionService questionService;

    @Override
    public void saveGroupLocalization(String groupId, String title, String langCode) {
        var group = groupService.findById(groupId);
        if (group == null) {
            return;
        }
        Map<String, String> localizedTitle = group.title() == null
                ? new HashMap<>()
                : new HashMap<>(group.title());
        localizedTitle.put(langCode, title);

        groupService.save(LocalizationDtoGroupBuilder.from(group)
                .title(localizedTitle)
                .build());
    }

    @Override
    public void saveQuestionLocalization(LocalizationDto localizationDto) {

    }

    @Override
    public void deleteGroupLocalization(String groupId, String code) {

    }

    @Override
    public void deleteQuestionLocalization(String questionId, String code) {

    }

    private static final class LocalizationDtoGroupBuilder {
        private final String groupId;
        private final String name;
        private Map<String, String> title;
        private final String parentId;

        private LocalizationDtoGroupBuilder(String groupId, String name, Map<String, String> title, String parentId) {
            this.groupId = groupId;
            this.name = name;
            this.title = title;
            this.parentId = parentId;
        }

        static LocalizationDtoGroupBuilder from(ru.vsu.core.model.dto.GroupDto group) {
            return new LocalizationDtoGroupBuilder(group.groupId(), group.name(), group.title(), group.parentId());
        }

        LocalizationDtoGroupBuilder title(Map<String, String> title) {
            this.title = title;
            return this;
        }

        ru.vsu.core.model.dto.GroupDto build() {
            return ru.vsu.core.model.dto.GroupDto.builder()
                    .groupId(groupId)
                    .name(name)
                    .title(title)
                    .parentId(parentId)
                    .build();
        }
    }
}
