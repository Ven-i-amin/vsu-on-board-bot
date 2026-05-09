package ru.vsu.core.component.bootstrap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(0)
@RequiredArgsConstructor
public class DatabaseResetBootstrap implements ApplicationRunner {
    private final MongoTemplate mongoTemplate;

    @Value("${app.database.reset-on-startup:false}")
    private boolean resetOnStartup;

    @Override
    public void run(ApplicationArguments args) {
        if (!resetOnStartup) {
            return;
        }

        String databaseName = mongoTemplate.getDb().getName();
        log.warn("Resetting Mongo database '{}' because app.database.reset-on-startup=true", databaseName);
        mongoTemplate.getDb().drop();
    }
}
