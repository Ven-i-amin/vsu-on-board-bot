package ru.vsu.core.bootstrap;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.vsu.core.service.GroupService;

@Component
@AllArgsConstructor
public class RootGroupBootstrap {
    private final GroupService groupService;

    @PostConstruct
    public void ensureRootGroupExists() {
        groupService.createRootIfMissing();
    }
}
