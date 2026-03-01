package com.ashutosh.fylex.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


public record RoomResponse(String roomCode,LocalDateTime createdAt,LocalDateTime expiresAt) {
}
