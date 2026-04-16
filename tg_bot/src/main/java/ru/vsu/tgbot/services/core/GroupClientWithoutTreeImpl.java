package ru.vsu.tgbot.services.core;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.vsu.contract.model.response.GroupResponseDto;
import ru.vsu.tgbot.components.mapper.CoreResponseMapper;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class GroupClientWithoutTreeImpl implements GroupClientWithoutTree{
    private final WebClient coreClient;
    private final CoreResponseMapper coreResponseMapper;

    @Override
    public GroupResponseDto getQuestionGroup(String groupName, String language) {
        return null;
    }

    @Override
    public GroupResponseDto getGroupWithDepth(String groupName, Integer depth, String language) {
        return null;
    }

    @Override
    public List<GroupResponseDto> getGroupsByNameWithDepth(String groupName, Integer depth, String language) {
        try {
            List<GroupResponseDto> groups = coreClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/bot/group/{groupName}")
                            .queryParam("depth", depth)
                            .build(groupName)
                    )
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<GroupResponseDto>>() {})
                    .block();

            return groups;
        } catch (RuntimeException ex) {
            log.warn("Failed to fetch group by name {} from core", groupName, ex);
           return null;
        }
    }

    @Override
    public List<GroupResponseDto> getInnerGroups(String groupName, String language) {
        return List.of();
    }

    @Override
    public List<GroupResponseDto> getInnerGroupsForEachGroup(List<String> groupNames, String language) {
        return List.of();
    }

    @Override
    public GroupResponseDto getStartGroup() {
        return null;
    }
}
