package com.ld.springsecurity.controller;

import com.ld.springsecurity.model.User;
import com.ld.springsecurity.repo.UserRepository;
import com.ld.springsecurity.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<User> authenticatedUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(currentUser);
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> allUser(){
        List<User> userList = userService.allUsers();
        return ResponseEntity.ok(userList);
    }

    @GetMapping("/")
    public ResponseEntity<String> helloworld(){
        return ResponseEntity.ok("Hello world");
    }
}
