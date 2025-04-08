package com.ld.springsecurity.response;

import com.ld.springsecurity.model.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private String username;
    private String email;
    private Role role;
    private String token;
    private Long expiresIn;

    public LoginResponse(String username, String email, Role role, String token, Long expiresIn) {
        this.username = username;
        this.email = email;
        this.role = role;
        this.token = token;
        this.expiresIn = expiresIn;
    }

}
