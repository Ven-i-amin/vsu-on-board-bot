package ru.vsu.core.model.request;

import lombok.Data;

import java.io.InputStream;

@Data
public class QuestionFileRequest {
    private String fileName;
    private InputStream fileData;
}
