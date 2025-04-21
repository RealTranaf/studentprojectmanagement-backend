package com.ld.springsecurity.service;

import com.ld.springsecurity.dto.CreateRoomDto;
import com.ld.springsecurity.model.Room;
import com.ld.springsecurity.repo.RoomRepository;
import org.springframework.stereotype.Service;

@Service
public class RoomService {
    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public Room createRoom(CreateRoomDto input){
        Room room = new Room();
        room.setName(input.getName());
        return roomRepository.save(room);
    }
}
