package ru.vsu.tgbot.services.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.vsu.contract.model.response.GroupResponseDto;
import ru.vsu.contract.model.response.QuestionResponseDto;
import ru.vsu.tgbot.components.mapper.CoreResponseMapper;
import ru.vsu.tgbot.model.dto.GroupDto;
import ru.vsu.tgbot.model.dto.QuestionDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GroupServiceImpl implements GroupService {
    private final WebClient coreClient;
    private final CoreResponseMapper coreResponseMapper;

    public GroupServiceImpl(
            @Qualifier("coreClient") WebClient coreClient,
            CoreResponseMapper coreResponseMapper
    ) {
        this.coreClient = coreClient;
        this.coreResponseMapper = coreResponseMapper;
    }

    @Override
    public GroupDto getRootGroup() {
        try {
            GroupDto group = coreClient.get()
                    .uri("/group/root")
                    .retrieve()
                    .bodyToMono(GroupResponseDto.class)
                    .map(coreResponseMapper::toGroupDto)
                    .block();
            return group == null ? placeholderGroup("root") : group;
        } catch (RuntimeException ex) {
            log.warn("Failed to fetch root group from core", ex);
            return placeholderGroup("root");
        }
    }

    @Override
    public GroupDto getGroup(String groupName) {
        try {
            GroupDto group = coreClient.get()
                    .uri("/group/{groupName}", groupName)
                    .retrieve()
                    .bodyToMono(GroupResponseDto.class)
                    .map(coreResponseMapper::toGroupDto)
                    .block();
            return group == null ? placeholderGroup(groupName) : group;
        } catch (RuntimeException ex) {
            log.warn("Failed to fetch group '{}' from core", groupName, ex);
            return placeholderGroup(groupName);
        }
    }

    @Override
    public List<GroupDto> getGroupChildren(String groupName) {
        try {
            List<GroupDto> groups = coreClient.get()
                    .uri("/group/{groupName}/children", groupName)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<GroupResponseDto>>() {})
                    .map(coreResponseMapper::toGroupDtoList)
                    .block();
            return groups == null ? List.of() : groups;
        } catch (RuntimeException ex) {
            log.warn("Failed to fetch children of group '{}' from core", groupName, ex);
            return List.of();
        }
    }

    @Override
    public List<QuestionDto> getGroupQuestions(String groupName) {
        try {
            List<QuestionDto> questions = coreClient.get()
                    .uri("/question/group/{groupName}", groupName)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<QuestionResponseDto>>() {})
                    .map(coreResponseMapper::toQuestionDtoList)
                    .block();
            return questions == null ? List.of() : questions;
        } catch (RuntimeException ex) {
            log.warn("Failed to fetch questions for group '{}' from core", groupName, ex);
            return List.of();
        }
    }

    @Override
    public GroupDto getGroupWithContent(String groupName) {
        GroupDto group = getGroup(groupName);
        group.setInnerGroups(new ArrayList<>(getGroupChildren(groupName)));
        group.setQuestions(new ArrayList<>(getGroupQuestions(groupName)));
        return group;
    }

    @Override
    public GroupDto getRootGroupWithContent() {
        GroupDto root = getRootGroup();
        root.setInnerGroups(new ArrayList<>(getGroupChildren(root.getName())));
        root.setQuestions(new ArrayList<>(getGroupQuestions(root.getName())));
        return root;
    }

    private GroupDto placeholderGroup(String groupName) {
        return GroupDto.builder()
                .name(groupName)
                .title(Map.of("ru", "Сервис временно недоступен"))
                .parents(new ArrayList<>())
                .innerGroups(new ArrayList<>())
                .questions(new ArrayList<>())
                .build();
    }
}
