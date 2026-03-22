package ru.vsu.tgbot.services.core;

import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.vsu.tgbot.components.mapper.CoreResponseMapper;
import ru.vsu.tgbot.model.dto.GroupDto;
import ru.vsu.tgbot.model.response.GroupResponseDto;

import java.util.List;

@Service
@AllArgsConstructor
public class GroupServiceImpl implements GroupService {
    private final WebClient coreClient;
    private final CoreResponseMapper coreResponseMapper;

    @Override
    public GroupDto getQuestionGroup(String groupId, String language) {
        return coreClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/group/{groupId}")
                        .queryParam("langСode", language)
                        .build(groupId)
                )
                .retrieve()
                .bodyToMono(GroupResponseDto.class)
                .map(coreResponseMapper::toGroupDto)
                .block();
    }

    @Override
    public GroupDto getGroupWithDepth(String groupId, Integer depth, String language) {
        return coreClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/group/{groupId}")
                        .queryParam("langCode", language)
                        .queryParam("depth", depth)
                        .build(groupId)
                )
                .retrieve()
                .bodyToMono(GroupResponseDto.class)
                .map(coreResponseMapper::toGroupDto)
                .block();
    }

    @Override
    public List<GroupDto> getInnerGroups(String thisGroupId, String language) {
        return coreClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/group/{groupId}/inner")
                        .queryParam("langCode", language)
                        .build(thisGroupId)
                )
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<GroupResponseDto>>() {})
                .map(coreResponseMapper::toGroupDtoList)
                .block();
    }

     @Override
     public List<GroupDto> getInnerGroupsForEachGroup(List<String> groupIds, String language) {
        return coreClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/group/list")
                        .queryParam("langCode", language)
                        .build()
                )
                .bodyValue(groupIds)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<GroupResponseDto>>() {})
                .map(coreResponseMapper::toGroupDtoList)
                .block();
     }

    @Override
    public GroupDto getStartGroup() {
        return coreClient.get()
                .uri("/group/start")
                .retrieve()
                .bodyToMono(GroupResponseDto.class)
                .map(coreResponseMapper::toGroupDto)
                .block();
    }

    @Override
    public GroupDto getStartGroup(String language) {
        return coreClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/group/start")
                        .queryParam("langCode", language)
                        .build()
                )
                .retrieve()
                .bodyToMono(GroupResponseDto.class)
                .map(coreResponseMapper::toGroupDto)
                .block();
    }
}
