package com.ld.springsecurity.controller;

import com.ld.springsecurity.dto.CreateRoomDto;
import com.ld.springsecurity.model.Room;
import com.ld.springsecurity.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/rooms")
public class RoomController{
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> createRoom(@RequestBody CreateRoomDto createRoomDto){
        Room room = roomService.createRoom(createRoomDto);
        return ResponseEntity.ok(room);
    }

}
