package com.ashutosh.fylex.dto;

import java.time.LocalDateTime;

public record FileUploadResponse(Long fileId, String originalFileName, Long size, LocalDateTime uploadTime) {
}
