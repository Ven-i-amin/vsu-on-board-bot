package ru.vsu.tgbot.services.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.vsu.contract.model.response.GroupResponseDto;
import ru.vsu.tgbot.components.mapper.CoreResponseMapper;
import ru.vsu.tgbot.model.dto.GroupDto;

import java.util.List;
import java.util.Map;

@Service
public class GroupClientImpl implements GroupClient {
    private static final Logger log = LoggerFactory.getLogger(GroupClientImpl.class);
    private final WebClient coreClient;
    private final CoreResponseMapper coreResponseMapper;

    public GroupClientImpl(WebClient coreClient, CoreResponseMapper coreResponseMapper) {
        this.coreClient = coreClient;
        this.coreResponseMapper = coreResponseMapper;
    }

    @Override
    public GroupDto getQuestionGroup(String groupName, String language) {
        return getGroupWithDepth(groupName, 0, language);
    }

    @Override
    public GroupDto getGroupWithDepth(String groupName, Integer depth, String language) {
        return getGroupByNameWithDepth(groupName, depth, language);
    }

    @Override
    public GroupDto getGroupByNameWithDepth(String groupName, Integer depth, String language) {
        try {
            List<GroupDto> groups = coreClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/bot/group/{groupName}")
                            .queryParam("depth", depth)
                            .build(groupName)
                    )
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<GroupResponseDto>>() {})
                    .map(coreResponseMapper::toGroupDtoList)
                    .block();
            GroupDto group = extractRootGroup(groups, groupName);
            if (group == null) {
                return placeholderGroup(groupName);
            }
            return group;
        } catch (RuntimeException ex) {
            log.warn("Failed to fetch group by name {} from core", groupName, ex);
            return placeholderGroup(groupName);
        }
    }

    @Override
    public List<GroupDto> getInnerGroups(String groupName, String language) {
        try {
            List<GroupDto> groups = coreClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/bot/group/{groupName}/inner")
                            .build(groupName)
                    )
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<GroupResponseDto>>() {})
                    .map(coreResponseMapper::toGroupDtoList)
                    .block();
            return groups == null ? List.of() : groups;
        } catch (RuntimeException ex) {
            log.warn("Failed to fetch inner groups for {}", groupName, ex);
            return List.of();
        }
    }

    @Override
    public List<GroupDto> getInnerGroupsForEachGroup(List<String> groupNames, String language) {
        try {
            List<GroupDto> groups = coreClient.post()
                    .uri("/bot/group/list")
                    .bodyValue(groupNames)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<GroupResponseDto>>() {})
                    .map(coreResponseMapper::toGroupDtoList)
                    .block();
            return groups == null ? List.of() : groups;
        } catch (RuntimeException ex) {
            log.warn("Failed to fetch nested groups from core", ex);
            return List.of();
        }
    }

    @Override
    public GroupDto getStartGroup() {
        try {
            List<GroupDto> groups = coreClient.get()
                    .uri("/bot/group/start")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<GroupResponseDto>>() {})
                    .map(coreResponseMapper::toGroupDtoList)
                    .block();
            GroupDto group = extractRootGroup(groups, "start");
            if (group == null) {
                return placeholderGroup("start");
            }
            return group;
        } catch (RuntimeException ex) {
            log.warn("Failed to fetch start group from core", ex);
            return placeholderGroup("start");
        }
    }

    private GroupDto extractRootGroup(List<GroupDto> groups, String defaultName) {
        if (groups == null || groups.isEmpty()) {
            return null;
        }

        GroupDto rootGroup = groups.stream()
                .filter(group -> group.getParentName() == null)
                .findFirst()
                .orElse(groups.getFirst());

        if (rootGroup.getName() == null || rootGroup.getName().isBlank()) {
            rootGroup.setName(defaultName);
        }

        return rootGroup;
    }

    private GroupDto placeholderGroup(String groupName) {
        String langCode = "ru";
        return GroupDto.builder()
                .name(groupName)
                .title(Map.of(langCode, "Сервис временно недоступен"))
                .innerGroups(new java.util.ArrayList<>())
                .questions(new java.util.ArrayList<>())
                .build();
    }
}
