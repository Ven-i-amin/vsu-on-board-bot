package ru.vsu.core.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Map;

public class UiMessage {
    @Id
    String id;
    @Indexed(unique = true)
    String name;
    Map<String, String> text;
}
