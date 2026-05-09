package ru.vsu.core.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("questions")
public class Question {
    @Id
    private String questionId;
    private String name;
    private String groupName;
    private Map<String, String> title;
    private Map<String, String> text;
    @Builder.Default
    private Integer using = 0;
}
