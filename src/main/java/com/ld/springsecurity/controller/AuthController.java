package com.ld.springsecurity.controller;

import com.ld.springsecurity.dto.LoginUserDto;
import com.ld.springsecurity.dto.MessageResponse;
import com.ld.springsecurity.dto.RegisterUserDto;
import com.ld.springsecurity.dto.VerifyUserDto;
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

//    @PostMapping("/signup")
//    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto){
//        User registerUser = authService.signup(registerUserDto);
//        return ResponseEntity.ok(registerUser);
//    }

    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody RegisterUserDto registerUserDto){
        String signupStatus = authService.signup(registerUserDto);
        if (signupStatus.equals("emailDupe")){
            return ResponseEntity.badRequest().body(new MessageResponse("This email has already been registered"));
        } else if (signupStatus.equals("usernameDupe")){
            return ResponseEntity.badRequest().body(new MessageResponse("This username has already been taken"));
        }
        return ResponseEntity.ok(new MessageResponse("User registered successfully"));
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto){
//        User authenticatedUser = authService.auth(loginUserDto);
//        String jwttoken = jwtService.generateToken(authenticatedUser);
//        LoginResponse loginResponse = new LoginResponse(jwttoken, jwtService.getJwtExpiration());
        LoginResponse loginResponse = authService.auth(loginUserDto);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserDto verifyUserDto){
        try{
            authService.verifyUser(verifyUserDto);
            return ResponseEntity.ok("Account verified successfully");
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resendCode(@RequestParam String email){
        try{
            authService.resendCode(email);
            return ResponseEntity.ok("Verification code sent.");
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
