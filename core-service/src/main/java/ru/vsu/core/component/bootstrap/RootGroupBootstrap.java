package ru.vsu.core.component.bootstrap;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.vsu.core.service.GroupService;

@Component
@Order(20)
@RequiredArgsConstructor
public class RootGroupBootstrap implements ApplicationRunner {
    private final GroupService groupService;

    @Override
    public void run(ApplicationArguments args) {
        groupService.createRootIfMissing();
    }
}
