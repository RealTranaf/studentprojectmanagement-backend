package com.ld.springsecurity.service;

import com.ld.springsecurity.dto.CreateRoomDto;
import com.ld.springsecurity.model.Room;
import com.ld.springsecurity.model.User;
import com.ld.springsecurity.model.ClassType;
import com.ld.springsecurity.repo.RoomRepository;
import com.ld.springsecurity.repo.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RoomService {
    private final RoomRepository roomRepository;

    private final UserRepository userRepository;
    public RoomService(RoomRepository roomRepository, UserRepository userRepository) {
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    public List<Room> getRoomsByUser(String username){
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()){
            User user = optionalUser.get();
            return new ArrayList<>(user.getRooms());
        }
        else {
            throw new RuntimeException("User not found");
        }
    }

    public Room createRoom(CreateRoomDto input, String createdBy){
        Optional<User> optionalUser = userRepository.findByUsername(createdBy);
        if (optionalUser.isPresent()){
            User teacher = optionalUser.get();
            Room room = new Room();
            room.setName(input.getName());
            try {
                ClassType type = ClassType.valueOf(input.getType());
                room.setType(type);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid room type. Must be CLASS_1, CLASS_2, CLASS_3, or CLASS_F.");
            }
            room.setCreatedBy(teacher);
            room.getUsers().add(teacher);
            return roomRepository.save(room);
        }
        else {
            throw new RuntimeException("User not found");
        }
    }

    public Room getRoomDetail(String roomId){
        Optional<Room> optionalRoom = roomRepository.findById(roomId);
        if (optionalRoom.isPresent()){
            Room room = optionalRoom.get();

            return room;
        }
        else {
            throw new RuntimeException("Room not found");
        }
    }

    public void addUsersToRoom(String roomId, List<String> usernameList){
        Optional<Room> optionalRoom = roomRepository.findById(roomId);
        if (optionalRoom.isPresent()){
            Room room = optionalRoom.get();
            List<User> userList = userRepository.findByUsernameIn(usernameList);
            if (userList.isEmpty()){
                throw new RuntimeException("No valid users found for the provided usernames!");
            }
            room.getUsers().addAll(userList);
            roomRepository.save(room);
        }
        else {
            throw new RuntimeException("Room not found!");
        }
    }

    public void removeUsersFromRoom(String roomId, List<String> usernameList){
        Optional<Room> optionalRoom = roomRepository.findById(roomId);
        if (optionalRoom.isPresent()){
            Room room = optionalRoom.get();
            List<User> userList = userRepository.findByUsernameIn(usernameList);
            if (userList.isEmpty()){
                throw new RuntimeException("No valid users found for the provided usernames!");
            }
            room.getUsers().removeAll(userList);
            roomRepository.save(room);
        }
        else {
            throw new RuntimeException("Room not found!");
        }
    }
    public void updateRoom(String roomId, CreateRoomDto input, String username) {
        Optional<Room> optionalRoom = roomRepository.findById(roomId);
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalRoom.isPresent() && optionalUser.isPresent()) {
            Room room = optionalRoom.get();
            User user = optionalUser.get();
            if (!room.getCreatedBy().getUsername().equals(username)) {
                throw new RuntimeException("You are not the author of this poll");
            }
            room.setName(input.getName());
            try {
                ClassType type = ClassType.valueOf(input.getType());
                room.setType(type);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid room type. Must be CLASS_1, CLASS_2, CLASS_3, or CLASS_F.");
            }
            roomRepository.save(room);
        } else {
            throw new RuntimeException("Room or user not found!");
        }
    }
}
