package com.school.mini.validation;

import org.springframework.web.multipart.MultipartFile;

public class FileUploadValidator {

    public static void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must not be null or empty");
        }

        if (!file.getOriginalFilename().endsWith(".csv")) {
            throw new IllegalArgumentException("Only CSV files are allowed");
        }
    }
}
