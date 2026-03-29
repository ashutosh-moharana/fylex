package com.ashutosh.fylex.controller;


import com.ashutosh.fylex.dto.CreateRoomResponse;
import com.ashutosh.fylex.dto.DeleteFileResponse;
import com.ashutosh.fylex.dto.FileUploadResponse;
import com.ashutosh.fylex.dto.RoomDetailsResponse;
import com.ashutosh.fylex.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @PostMapping
    public ResponseEntity<CreateRoomResponse> createRoom(){
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.createRoom());

    }

    @GetMapping("/{code}")
    public ResponseEntity<RoomDetailsResponse> getRoomByCode(@PathVariable String code){
        return ResponseEntity.ok(roomService.getRoomByCode(code));
    }

    @PostMapping("/{code}/files")
    public ResponseEntity<FileUploadResponse> uploadFile(
            @PathVariable String code,
            @RequestParam("file") MultipartFile file
            ){
    return ResponseEntity.status(HttpStatus.CREATED).body(roomService.uploadFile(code,file));
    }

    @GetMapping("/{code}/files/{fileId}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String code,
            @PathVariable Long fileId
    ){
        return roomService.downloadFile(code,fileId);
    }

    @DeleteMapping("/{code}/files/{fileId}")
    public ResponseEntity<DeleteFileResponse> deleteFile(
            @PathVariable String code,
            @PathVariable Long fileId
    ){

        return ResponseEntity.ok(roomService.deleteFile(code,fileId));
    }

}
