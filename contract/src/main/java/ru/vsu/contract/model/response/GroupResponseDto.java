package ru.vsu.contract.model.response;

import java.util.List;
import java.util.Map;

public record GroupResponseDto(
        String name,
        Map<String, String> title,
        List<String> parents
) {
}