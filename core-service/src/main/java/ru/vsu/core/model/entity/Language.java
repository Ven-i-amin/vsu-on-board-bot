package ru.vsu.core.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("languages")
public class Language {
    @Id
    private String id;
    @Indexed(unique = true)
    private String code;
    private Map<String, String> name;
}
