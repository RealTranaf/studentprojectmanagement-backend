package com.ld.springsecurity.response;

import com.ld.springsecurity.model.Role;
import com.ld.springsecurity.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDetailResponse {
    private String id;
    private String username;
    private String email;
    private Role role;

    public UserDetailResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole();
    }
}
