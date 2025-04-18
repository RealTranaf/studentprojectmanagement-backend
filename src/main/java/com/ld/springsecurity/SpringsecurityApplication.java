package com.ld.springsecurity;

import com.ld.springsecurity.dto.RegisterUserDto;
import com.ld.springsecurity.model.Role;
import com.ld.springsecurity.service.AuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringsecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringsecurityApplication.class, args);
    }

//    @Bean
//    public CommandLineRunner commandLineRunner(AuthService authService){
//        return args -> {
//            RegisterUserDto admin = new RegisterUserDto("admin@mail.com", "password", "Admin", Role.ADMIN);
//            authService.signup(admin);
//
//            RegisterUserDto teacher = new RegisterUserDto("teacher@mail.com", "password", "Teacher", Role.TEACHER);
//            authService.signup(teacher);
//        };
//    }

}
