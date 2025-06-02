package com.ld.springsecurity.dto;

import com.ld.springsecurity.model.Role;
import com.ld.springsecurity.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    private String id;
    private String username;
    private String email;
    private Role role;
    public UserDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole();
    }
}
