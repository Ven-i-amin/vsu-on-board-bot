package ru.vsu.core.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.vsu.core.component.tempate.MongoTemplate;

@Configuration
public class MongoConfig {
    @Bean
    public MongoTemplate getMongoTemplate() {
        return new MongoTemplate();
    }
}
