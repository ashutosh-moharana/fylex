package com.ashutosh.fylex.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Component
public class FileValidator {

    private static final Logger logger = LoggerFactory.getLogger(FileValidator.class);

    @Value("${app.max-file-size:52428800}")
    private long maxFileSize;

    @Value("${app.allowed-extensions:pdf,docx,xlsx,jpg,png,txt}")
    private String allowedExtensions;

    public void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            logger.warn("Attempted to upload empty file");
            throw new IllegalArgumentException("File cannot be empty");
        }

        // Validate file size
        if (file.getSize() > maxFileSize) {
            logger.warn("File size exceeds limit: {} bytes (max: {})", file.getSize(), maxFileSize);
            throw new IllegalArgumentException(
                    "File size exceeds maximum limit of " + (maxFileSize / (1024 * 1024)) + " MB"
            );
        }

        // Validate file extension
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            logger.warn("Invalid filename: {}", originalFilename);
            throw new IllegalArgumentException("Invalid filename");
        }

        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        List<String> allowedExtList = Arrays.asList(allowedExtensions.split(","));

        if (!allowedExtList.contains(fileExtension)) {
            logger.warn("File type not allowed: {} (allowed types: {})", fileExtension, allowedExtensions);
            throw new IllegalArgumentException(
                    "File type ." + fileExtension + " is not allowed. Allowed types: " + allowedExtensions
            );
        }
    }
}
