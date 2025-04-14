package com.ld.springsecurity.controller;

import com.ld.springsecurity.model.User;
import com.ld.springsecurity.repo.UserRepository;
import com.ld.springsecurity.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

//    @GetMapping("/me")
//    public ResponseEntity<User> authenticatedUser(){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        User currentUser = (User) authentication.getPrincipal();
//        return ResponseEntity.ok(currentUser);
//    }

    @GetMapping("/me")
    public ResponseEntity<Optional<User>> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails){
        String name = userDetails.getUsername();
        Optional<User> user = userRepository.findByUsername(name);
        return ResponseEntity.ok(user);
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
