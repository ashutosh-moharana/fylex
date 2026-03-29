package com.ashutosh.fylex.repo;

import com.ashutosh.fylex.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByRoomCode(String roomCode);
    
    @Modifying
    long deleteByExpiresAtBefore(LocalDateTime now);
}
