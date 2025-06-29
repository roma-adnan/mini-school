package com.school.mini.controller;

import com.school.mini.dto.FileUploadResponse;
import com.school.mini.service.FileUploadService;
import com.school.mini.validation.FileUploadValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
@Slf4j
public class FileUploadController {

    @Autowired
    private  FileUploadService fileUploadService;

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponse> uploadFile(@RequestPart("file") MultipartFile file) {
        FileUploadValidator.validateFile(file);
        return ResponseEntity.ok(fileUploadService.upload(file));
    }

    @GetMapping("/status/{importId}")
    public ResponseEntity<FileUploadResponse> getStatus(@PathVariable String importId) {
        return ResponseEntity.ok(fileUploadService.getStatus(importId));
    }
}
