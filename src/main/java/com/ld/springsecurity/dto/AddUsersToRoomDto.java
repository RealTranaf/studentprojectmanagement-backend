package com.ld.springsecurity.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AddUsersToRoomDto {
    private List<String> usernames;

    public AddUsersToRoomDto(List<String> usernames) {
        this.usernames = usernames;
    }
}
