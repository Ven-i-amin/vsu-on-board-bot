package ru.vsu.tgbot.services.core;

import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.vsu.tgbot.model.response.GroupResponseDto;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Service
@AllArgsConstructor
public class GroupServiceImpl implements GroupService {
    private final WebClient coreClient;

    @Override
    public GroupResponseDto getQuestionGroup(String groupId, String language) {
        return coreClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/innerGroups/{groupId}")
                        .queryParam("lang", language)
                        .build(groupId)
                )
                .retrieve()
                .bodyToMono(GroupResponseDto.class)
                .block();
    }

    @Override
    public GroupResponseDto getGroupWithRecursion(String groupId, Integer recursion, String language) {
        return coreClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/innerGroups/{groupId}")
                        .queryParam("lang", language)
                        .queryParam("recursion", recursion)
                        .build(groupId)
                )
                .retrieve()
                .bodyToMono(GroupResponseDto.class)
                .block();
    }

    @Override
    public List<GroupResponseDto> getInnerGroups(String thisGroupId, String language) {
        return coreClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/innerGroups/{groupId}/inner")
                        .queryParam("lang", language)
                        .build(thisGroupId)
                )
                .retrieve()
                .bodyToMono(GroupResponseDto.class)
                .map(Arrays::asList)
                .block();
    }

     @Override
     public List<GroupResponseDto> getInnerGroupsForEachGroup(List<String> groupIds, String language) {
        return coreClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/innerGroups/list")
                        .queryParam("lang", language)
                        .build()
                )
                .bodyValue(groupIds)
                .retrieve()
                .bodyToMono(GroupResponseDto.class)
                .map(Arrays::asList)
                .block();
     }

    @Override
    public GroupResponseDto getStartGroup() {
        return coreClient.get()
                .uri("/group/start")
                .retrieve()
                .bodyToMono(GroupResponseDto.class)
                .block();
    }

    @Override
    public GroupResponseDto getStartGroup(String language) {
        return coreClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/innerGroups/start")
                        .queryParam("lang", language)
                        .build()
                )
                .retrieve()
                .bodyToMono(GroupResponseDto.class)
                .block();
    }
}
