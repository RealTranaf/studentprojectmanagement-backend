package com.ld.springsecurity.response;

import com.ld.springsecurity.model.Room;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomResponse {
    private String id;
    private String name;
    private String type;
    private String createdBy;
    public RoomResponse(Room room){
        this.id = room.getId();
        this.type = room.getType().getDisplayName();
        this.name = room.getName();
        this.createdBy = room.getCreatedBy().getUsername();
    }
}
