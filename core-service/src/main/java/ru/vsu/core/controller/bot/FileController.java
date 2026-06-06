package ru.vsu.core.controller.bot;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vsu.core.model.dto.QuestionFileDto;
import ru.vsu.core.service.storage.QuestionFileService;

@RestController
@RequestMapping("/file")
@AllArgsConstructor
public class FileController {
    private final QuestionFileService questionFileService;

    @GetMapping("/{fileHash}/content")
    public ResponseEntity<byte[]> getFileContent(@PathVariable String fileHash) {
        QuestionFileDto info = questionFileService.get(fileHash);
        if (info == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] content = questionFileService.getFile(fileHash);
        String fileName = info.fileName() != null ? info.fileName() : fileHash;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(content);
    }
}
