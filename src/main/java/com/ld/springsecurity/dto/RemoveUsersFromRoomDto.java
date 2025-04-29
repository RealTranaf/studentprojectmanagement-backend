package com.ld.springsecurity.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RemoveUsersFromRoomDto {
    private List<String> usernames;

    public RemoveUsersFromRoomDto(List<String> usernames) {
        this.usernames = usernames;
    }
}
