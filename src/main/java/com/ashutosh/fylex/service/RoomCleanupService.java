package com.ashutosh.fylex.service;

import com.ashutosh.fylex.repo.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RoomCleanupService {

    @Autowired
    RoomRepository roomRepository;

    @Scheduled(fixedRate = 600000)  // Run every 10 minutes
    @Transactional
    public void cleanExpiredRoom(){
        LocalDateTime now = LocalDateTime.now();
        roomRepository.deleteByExpiresAtBefore(now);
    }
}
