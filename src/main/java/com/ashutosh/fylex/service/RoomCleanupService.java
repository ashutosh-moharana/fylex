package com.ashutosh.fylex.service;

import com.ashutosh.fylex.repo.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RoomCleanupService {

    private static final Logger logger = LoggerFactory.getLogger(RoomCleanupService.class);

    @Autowired
    RoomRepository roomRepository;

    @Scheduled(fixedDelay = 600000, initialDelay = 60000)
    @Transactional
    public void cleanExpiredRoom() {
        try {
            LocalDateTime now = LocalDateTime.now();
            logger.info("Starting cleanup of expired rooms at: {}", now);

            long deletedCount = roomRepository.deleteByExpiresAtBefore(now);
            logger.info("Successfully deleted {} expired rooms", deletedCount);

        } catch (Exception e) {
            logger.error("Error occurred during room cleanup", e);
            // Don't rethrow - we want the scheduler to continue even if cleanup fails
        }
    }
}
