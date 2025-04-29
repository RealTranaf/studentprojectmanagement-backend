package com.ld.springsecurity.service;

import com.ld.springsecurity.dto.UserDto;
import com.ld.springsecurity.model.User;
import com.ld.springsecurity.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
    }

    public List<User> allUsers(){
        List<User> userList = new ArrayList<>();
        userRepository.findAll().forEach(userList::add);
        return userList;
    }

    public List<UserDto> searchUsers(String query){
        List<User> userList = userRepository.findTop5ByUsernameContainingIgnoreCase(query);
        List<UserDto> userDtoList = userList.stream()
                .map(user -> new UserDto(user))
                .collect(Collectors.toList());
        return userDtoList;
    }
}
