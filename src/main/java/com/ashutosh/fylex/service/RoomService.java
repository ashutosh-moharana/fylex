package com.ashutosh.fylex.service;

import com.ashutosh.fylex.dto.*;
import com.ashutosh.fylex.exception.FileNotFoundException;
import com.ashutosh.fylex.exception.FileStorageException;
import com.ashutosh.fylex.exception.RoomNotFoundException;
import com.ashutosh.fylex.model.FileMetaData;
import com.ashutosh.fylex.model.Room;
import com.ashutosh.fylex.repo.FileMetaDataRepository;
import com.ashutosh.fylex.repo.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RoomService {

    private static final Logger logger = LoggerFactory.getLogger(RoomService.class);

    private final RoomRepository roomRepository;
    private final FileMetaDataRepository fileMetaDataRepository;
    private final FileValidator fileValidator;

    private final Path uploadPath;

    public RoomService(RoomRepository roomRepository, FileMetaDataRepository fileMetaDataRepository, FileValidator fileValidator, @Value("${app.upload.dir:./uploads}") String uploadDir) {
        this.roomRepository = roomRepository;
        this.fileMetaDataRepository = fileMetaDataRepository;
        this.fileValidator = fileValidator;
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    public CreateRoomResponse createRoom() {
        String code = generateCode();
        LocalDateTime now = LocalDateTime.now();

        Room room = new Room(null, code, now, now.plusHours(24), new ArrayList<>());
        roomRepository.save(room);
        
        logger.info("Room created successfully with code: {}, expires at: {}", code, now.plusHours(24));
        return new CreateRoomResponse(room.getRoomCode(), room.getCreatedAt(), room.getExpiresAt());
    }
    public RoomDetailsResponse getRoomByCode(String code) {
        logger.debug("Fetching room details for code: {}", code);
        
        Room room = roomRepository.findByRoomCode(code)
                .orElseThrow(() -> {
                    logger.warn("Room not found with code: {}", code);
                    return new RoomNotFoundException("Room not found with code: " + code);
                });

        List<FileRespone> files = room.getFiles().stream()
                .map(file -> new FileRespone(
                        file.getId(),
                        file.getOriginalFileName(),
                        file.getSize(),
                        file.getUploadTime()
                )).toList();

        return new RoomDetailsResponse(
                room.getRoomCode(),
                room.getCreatedAt(),
                room.getExpiresAt(),
                files
        );
    }


    private String generateCode() {
        // Use SecureRandom for cryptographically secure randomness
        SecureRandom random = new SecureRandom();
        
        // Character set: uppercase, lowercase, and digits for better entropy
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        
        // Generate 10-character code (provides ~47 bits of entropy)
        // vs 6-character UUID substring (provides ~24 bits)
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            code.append(characters.charAt(random.nextInt(characters.length())));
        }
        
        return code.toString();
    }

    public FileUploadResponse uploadFile(String code, MultipartFile file) {
        logger.debug("Starting file upload for room code: {}, filename: {}", code, file.getOriginalFilename());
        
        fileValidator.validateFile(file);

        Room room = roomRepository.findByRoomCode(code)
                .orElseThrow(() -> {
                    logger.warn("Room not found with code: {} during file upload", code);
                    return new RoomNotFoundException("Room not found with code: " + code);
                });

        try {
            Files.createDirectories(uploadPath);

            String storedFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(storedFileName);

            file.transferTo(filePath.toFile());
            logger.info("File saved successfully: {} with stored name: {}", file.getOriginalFilename(), storedFileName);

            FileMetaData fileMetaData = new FileMetaData();
            fileMetaData.setOriginalFileName(file.getOriginalFilename());
            fileMetaData.setStoredFileName(storedFileName);
            fileMetaData.setSize(file.getSize());
            fileMetaData.setUploadTime(LocalDateTime.now());
            fileMetaData.setRoom(room);

            FileMetaData savedFile = fileMetaDataRepository.save(fileMetaData);
            logger.info("File metadata saved with ID: {}", savedFile.getId());

            return new FileUploadResponse(
                    savedFile.getId(),
                    savedFile.getOriginalFileName(),
                    savedFile.getSize(),
                    savedFile.getUploadTime()
            );

        } catch (IOException e) {
            logger.error("Failed to store file: {}", file.getOriginalFilename(), e);
            throw new FileStorageException("Failed to store file: " + e.getMessage(), e);
        }
    }

    public ResponseEntity<Resource> downloadFile(String code, Long fileId) {
        logger.debug("Downloading file ID: {} from room: {}", fileId, code);
        
        Room room = roomRepository.findByRoomCode(code)
                .orElseThrow(() -> {
                    logger.warn("Room not found with code: {} during download", code);
                    return new RoomNotFoundException("Room not found with code: " + code);
                });

        FileMetaData fileMetaData = fileMetaDataRepository.findByIdAndRoom(fileId, room)
                .orElseThrow(() -> {
                    logger.warn("File not found with ID: {} in room: {}", fileId, code);
                    return new FileNotFoundException("File not found with id: " + fileId);
                });

        Path filePath = uploadPath.resolve(fileMetaData.getStoredFileName());

        try {
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                logger.error("File not found on disk: {}", filePath);
                throw new FileNotFoundException("File not found on disk");
            }

            logger.info("File downloaded successfully: {}", fileMetaData.getOriginalFileName());
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + fileMetaData.getOriginalFileName() + "\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            logger.error("Failed to create URL resource for file: {}", filePath, e);
            throw new FileStorageException("Failed to download file: " + e.getMessage(), e);
        }
    }

    public DeleteFileResponse deleteFile(String code, Long fileId) {
        logger.debug("Deleting file ID: {} from room: {}", fileId, code);
        
        Room room = roomRepository.findByRoomCode(code)
                .orElseThrow(() -> {
                    logger.warn("Room not found with code: {} during deletion", code);
                    return new RoomNotFoundException("Room not found with code: " + code);
                });

        FileMetaData fileMetaData = fileMetaDataRepository.findByIdAndRoom(fileId, room)
                .orElseThrow(() -> {
                    logger.warn("File not found with ID: {} in room: {}", fileId, code);
                    return new FileNotFoundException("File not found with id: " + fileId);
                });

        Path filePath = uploadPath.resolve(fileMetaData.getStoredFileName());

        try {
            Files.deleteIfExists(filePath);
            logger.info("File deleted from disk: {}", fileMetaData.getStoredFileName());
        } catch (IOException e) {
            logger.error("Failed to delete file from disk: {}", filePath, e);
            throw new FileStorageException("Failed to delete file from disk: " + e.getMessage(), e);
        }

        fileMetaDataRepository.delete(fileMetaData);
        logger.info("File record deleted from database: {}", fileMetaData.getOriginalFileName());

        return new DeleteFileResponse("File Deleted Successfully!");

    }

    public FileListResponse getAllFiles(String code) {
        logger.debug("Fetching all files for room code: {}", code);
        Room room = roomRepository.findByRoomCode(code)
                .orElseThrow(() -> {
                    logger.warn("Room not found with code: {}", code);
                    return new RoomNotFoundException("Room not found with code: " + code);
                });

        List<FileRespone> files = room.getFiles().stream()
                .map(file -> new FileRespone(
                        file.getId(),
                        file.getOriginalFileName(),
                        file.getSize(),
                        file.getUploadTime()
                )).toList();

        return new FileListResponse(files);
    }
}
