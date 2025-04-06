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

    @Bean
    public CommandLineRunner commandLineRunner(AuthService authService){
        return args -> {
            RegisterUserDto admin = new RegisterUserDto("admin@mail.com", "password", "Admin", Role.ADMIN);
            System.out.println("Admin token: " + authService.signup(admin).getTokenList());

            RegisterUserDto manager = new RegisterUserDto("manager@mail.com", "password", "Manager", Role.MANAGER);
            System.out.println("Manager token: " + authService.signup(manager).getTokenList());
        };
    }


}
