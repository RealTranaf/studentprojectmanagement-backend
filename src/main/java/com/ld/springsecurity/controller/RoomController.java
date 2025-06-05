package com.ld.springsecurity.controller;

import com.ld.springsecurity.dto.AddUsersToRoomDto;
import com.ld.springsecurity.dto.CreateRoomDto;
import com.ld.springsecurity.model.Room;
import com.ld.springsecurity.response.CreateRoomResponse;
import com.ld.springsecurity.response.MessageResponse;
import com.ld.springsecurity.response.RoomDetailResponse;
import com.ld.springsecurity.response.RoomResponse;
import com.ld.springsecurity.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rooms")
public class RoomController{
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createRoom(@RequestBody CreateRoomDto createRoomDto, @AuthenticationPrincipal UserDetails userDetails){
        try{
            Room room = roomService.createRoom(createRoomDto, userDetails.getUsername());
            return ResponseEntity.ok(new CreateRoomResponse(room.getName()));
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping()
    public ResponseEntity<?> showRoomList(@AuthenticationPrincipal UserDetails userDetails){
        try{
            List<Room> roomList = roomService.getRoomsByUser(userDetails.getUsername());
            List<RoomResponse> roomListResponse = roomList.stream().map(RoomResponse::new).collect(Collectors.toList());
            return ResponseEntity.ok(roomListResponse);
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<?> getRoomDetails(@PathVariable String roomId){
        try{
            Room room = roomService.getRoomDetail(roomId);
            RoomDetailResponse roomDetailResponse = new RoomDetailResponse(room);
            return ResponseEntity.ok(roomDetailResponse);
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PutMapping("/{roomId}")
    public ResponseEntity<?> updateRoom(@PathVariable String roomId,
                                        @RequestBody CreateRoomDto createRoomDto,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        try {
            roomService.updateRoom(roomId, createRoomDto, userDetails.getUsername());
            return ResponseEntity.ok(new MessageResponse("Room updated successfully!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/{roomId}/add-users")
    public ResponseEntity<?> addUserToRoom(@PathVariable String roomId, @RequestBody AddUsersToRoomDto addUsersToRoomDto){
        try{
            roomService.addUsersToRoom(roomId, addUsersToRoomDto.getUsernames());
            return ResponseEntity.ok(new MessageResponse("Users added to the room successfully"));
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{roomId}/remove-users")
    public ResponseEntity<?> removeUsersFromRoom(@PathVariable String roomId, @RequestParam List<String> username){
        try{
            roomService.removeUsersFromRoom(roomId, username);
            return ResponseEntity.ok("User removed successfully");
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}
