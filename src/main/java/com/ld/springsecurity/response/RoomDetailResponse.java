package com.ld.springsecurity.response;

import com.ld.springsecurity.model.Room;
import com.ld.springsecurity.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class RoomDetailResponse {
    private String id;
    private String name;
    private String createdBy;
    private List<String> userList;

    public RoomDetailResponse(Room room){
        this.id = room.getId();
        this.name = room.getName();
        this.createdBy = room.getCreatedBy().getUsername();
        this.userList = room.getUsers().stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
    }
}
