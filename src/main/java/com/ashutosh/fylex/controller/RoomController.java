package com.ashutosh.fylex.controller;


import com.ashutosh.fylex.dto.RoomResponse;
import com.ashutosh.fylex.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(){
        return new ResponseEntity<>(roomService.createRoom(),HttpStatus.OK);
    }
}
