package ru.vsu.tgbot.services.core;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.vsu.contract.model.response.GroupResponseDto;
import ru.vsu.tgbot.components.mapper.CoreResponseMapper;
import ru.vsu.tgbot.model.dto.GroupDto;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class GroupServiceImpl implements GroupService {
    private final WebClient coreClient;
    private final CoreResponseMapper coreResponseMapper;

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
            GroupDto group = coreClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/group/{groupName}")
                            .queryParam("depth", depth)
                            .build(groupName)
                    )
                    .retrieve()
                    .bodyToMono(GroupResponseDto.class)
                    .map(coreResponseMapper::toGroupDto)
                    .block();
            return group == null ? placeholderGroup(groupName) : group;
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
                            .path("/group/{groupName}/inner")
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
                    .uri("/group/list")
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
            GroupDto group = coreClient.get()
                    .uri("/group/start")
                    .retrieve()
                    .bodyToMono(GroupResponseDto.class)
                    .map(coreResponseMapper::toGroupDto)
                    .block();
            return group == null ? placeholderGroup("start") : group;
        } catch (RuntimeException ex) {
            log.warn("Failed to fetch start group from core", ex);
            return placeholderGroup("start");
        }
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
