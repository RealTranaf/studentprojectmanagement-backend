package com.ld.springsecurity.dto;

import com.ld.springsecurity.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
public class RegisterUserDto {
    private String email;
    private String password;
    private String username;
    private Role role;
}
