package com.ashutosh.fylex.service;

import com.ashutosh.fylex.repo.RoomRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;



@SpringBootTest
public class RoomServiceTests {

    @Autowired
    RoomRepository roomRepository;
    @Test
    void testFindByUserName(){
        Assertions.assertNotNull(roomRepository.findByRoomCode("4336"));
    }
}
