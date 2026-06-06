package ru.vsu.core.controller.api;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import ru.vsu.core.model.dto.QuestionFileDto;
import ru.vsu.core.service.storage.QuestionFileService;

import java.io.IOException;

@RestController
@RequestMapping("/api/file")
@AllArgsConstructor
public class QuestionFileApiController {
    private final QuestionFileService questionFileService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public QuestionFileDto upload(@RequestParam("file") MultipartFile file) throws IOException {
        return questionFileService.save(file.getOriginalFilename(), file.getInputStream());
    }

    @GetMapping("/{fileHash}")
    public QuestionFileDto getFileInfo(@PathVariable String fileHash) {
        return questionFileService.get(fileHash);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public String handleMaxUploadSize() {
        return "File size exceeds the 50MB limit";
    }

    @GetMapping("/{fileHash}/content")
    public ResponseEntity<byte[]> getFileContent(@PathVariable String fileHash) {
        QuestionFileDto info = questionFileService.get(fileHash);
        if (info == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] content = questionFileService.getFile(fileHash);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + info.fileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(content);
    }
}
