package com.ld.springsecurity.response;

import com.ld.springsecurity.model.Room;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomListResponse {
    private String id;
    private String name;
    private String createdBy;
    public RoomListResponse(Room room){
        this.id = room.getId();
        this.name = room.getName();
        this.createdBy = room.getCreatedBy().getUsername();
    }
}
