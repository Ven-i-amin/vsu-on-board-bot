package ru.vsu.core.model.response;

public record LocalizedGroupResponseDto(
        String groupId,
        String name,
        String title,
        String parentId
) {

}
