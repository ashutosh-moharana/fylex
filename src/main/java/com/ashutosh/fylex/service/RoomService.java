package com.ashutosh.fylex.service;

import com.ashutosh.fylex.dto.*;
import com.ashutosh.fylex.exception.FileNotFoundException;
import com.ashutosh.fylex.exception.RoomNotFoundException;
import com.ashutosh.fylex.model.FileMetaData;
import com.ashutosh.fylex.model.Room;
import com.ashutosh.fylex.repo.FileMetaDataRepository;
import com.ashutosh.fylex.repo.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RoomService {

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    FileMetaDataRepository fileMetaDataRepository;

    public CreateRoomResponse createRoom() {

        String code = generateCode();
        LocalDateTime now = LocalDateTime.now();

        Room room = new
                Room(null,
                code,
                now,
               now.plusHours(24),  // Room expires after 24 hours
                new ArrayList<>());

        roomRepository.save(room);
        return new CreateRoomResponse(room.getRoomCode(),room.getCreatedAt(),room.getExpiresAt());

    }
    public RoomDetailsResponse getRoomByCode(String code){
        Room room = roomRepository.findByRoomCode(code)
                .orElseThrow(()-> new RuntimeException("Room not found with code :"+code));


        List<FileRespone> files = room.getFiles().stream()
                .map(file->new FileRespone(
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


    private String generateCode(){
        return UUID.randomUUID().toString().substring(0,6).toUpperCase();
    }

    public FileUploadResponse uploadFile(String code, MultipartFile file) {
        Room room = roomRepository.findByRoomCode(code)
                .orElseThrow(() -> new RoomNotFoundException("Room not found with code: " + code));

        try {
            String uploadDir = System.getProperty("user.dir") + "/uploads";
            Files.createDirectories(Paths.get(uploadDir));

            String storedFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, storedFileName);

            file.transferTo(filePath.toFile());

            FileMetaData fileMetaData = new FileMetaData();
            fileMetaData.setOriginalFileName(file.getOriginalFilename());
            fileMetaData.setStoredFileName(storedFileName);
            fileMetaData.setSize(file.getSize());
            fileMetaData.setUploadTime(LocalDateTime.now());
            fileMetaData.setRoom(room);

            FileMetaData savedFile = fileMetaDataRepository.save(fileMetaData);

            return new FileUploadResponse(
                    savedFile.getId(),
                    savedFile.getOriginalFileName(),
                    savedFile.getSize(),
                    savedFile.getUploadTime()
            );

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file");
        }
    }

    public ResponseEntity<Resource> downloadFile(String code, Long fileId) {
        Room room = roomRepository.findByRoomCode(code)
                .orElseThrow(()-> new RoomNotFoundException("Room not found with code: "+code));

        FileMetaData fileMetaData = fileMetaDataRepository.findByIdAndRoom(fileId,room)
                .orElseThrow(()-> new FileNotFoundException("File not found with id: "+fileId));

        String uploadDir = Paths.get(System.getProperty("user.dir"), "uploads").toString();
        Path filePath = Paths.get(uploadDir, fileMetaData.getStoredFileName());

        try {
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                throw new RuntimeException("File not found on disk");
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + fileMetaData.getOriginalFileName() + "\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            throw new RuntimeException("Failed to download file");
        }
    }

    public DeleteFileResponse deleteFile(String code, Long fileId) {

        Room room = roomRepository.findByRoomCode(code)
                .orElseThrow(()->new RoomNotFoundException("Room not found with code: "+code));

        FileMetaData fileMetaData = fileMetaDataRepository.findByIdAndRoom(fileId,room)
                .orElseThrow(()->new FileNotFoundException("File not found with id: "+fileId));

        String uploadDir = Paths.get(System.getProperty("user.dir"), "uploads").toString();
        Path filePath = Paths.get(uploadDir, fileMetaData.getStoredFileName());

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file from disk: " + e.getMessage());
        }

        fileMetaDataRepository.delete(fileMetaData);
        return new DeleteFileResponse("File Deleted Successfully!");

    }
}
