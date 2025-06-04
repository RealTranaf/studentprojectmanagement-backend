package com.ld.springsecurity.response;

import com.ld.springsecurity.dto.UserDto;
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
    private String type;
    private List<UserDto> userList;

    public RoomDetailResponse(Room room){
        this.id = room.getId();
        this.name = room.getName();
        this.type = room.getType().getDisplayName();
        this.createdBy = room.getCreatedBy().getUsername();
        this.userList = room.getUsers().stream()
                .map(user -> new UserDto(user))
                .collect(Collectors.toList());
    }
}
