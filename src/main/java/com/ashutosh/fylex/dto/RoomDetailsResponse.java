package com.ashutosh.fylex.dto;

import java.time.LocalDateTime;
import java.util.List;

public record RoomDetailsResponse(
        String roomCode,
        LocalDateTime createdAt,
        LocalDateTime expiresAt,
        List<FileRespone> files
) {

}
