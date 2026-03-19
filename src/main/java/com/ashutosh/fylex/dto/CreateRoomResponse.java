package com.ashutosh.fylex.dto;


import java.time.LocalDateTime;


public record CreateRoomResponse(String roomCode, LocalDateTime createdAt, LocalDateTime expiresAt) {
}
