package com.ashutosh.fylex.service;

import com.ashutosh.fylex.dto.RoomResponse;
import com.ashutosh.fylex.model.Room;
import com.ashutosh.fylex.repo.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

@Service
public class RoomService {

    @Autowired
    RoomRepository roomRepository;

    public RoomResponse createRoom() {

        String code = generateCode();

        Room room = new
                Room(null,
                code,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(24),
                new ArrayList<>());

        roomRepository.save(room);
        return new RoomResponse(room.getRoomCode(),room.getCreatedAt(),room.getExpiresAt());

    }

    private String generateCode(){
        return UUID.randomUUID().toString().substring(0,6).toUpperCase();
    }
}
