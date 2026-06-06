package ru.vsu.core.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("docs")
public class QuestionFile {
    @Id
    private String documentId;

    @Builder.Default
    private String fileHash = "";
    private String fileName;
    @Field("usage")
    @Builder.Default
    private Integer fileUsage = 0;
    @Builder.Default
    private Instant uploadedAt = Instant.now();
}
