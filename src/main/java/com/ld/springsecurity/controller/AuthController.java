package com.ld.springsecurity.controller;

import com.ld.springsecurity.dto.*;
import com.ld.springsecurity.model.User;
import com.ld.springsecurity.response.LoginResponse;
import com.ld.springsecurity.service.AuthService;
import com.ld.springsecurity.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;

    private final AuthService authService;

    public AuthController(JwtService jwtService, AuthService authService) {
        this.jwtService = jwtService;
        this.authService = authService;
    }


    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody RegisterUserDto registerUserDto){
        try{
            authService.signup(registerUserDto);
            return ResponseEntity.ok(new MessageResponse("User registered successfully"));
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody LoginUserDto loginUserDto){
        try{
            LoginResponse loginResponse = authService.auth(loginUserDto);
            return ResponseEntity.ok(loginResponse);
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }


    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserDto verifyUserDto){
        try{
            authService.verifyUser(verifyUserDto);
            return ResponseEntity.ok(new MessageResponse("Account verified successfully"));
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resendCode(@RequestBody ResendDto resendDto){
        try{
            authService.resendCode(resendDto);
            return ResponseEntity.ok(new MessageResponse("Verification code sent."));
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }


}
