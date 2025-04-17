package com.ld.springsecurity.service;

import com.ld.springsecurity.dto.LoginUserDto;
import com.ld.springsecurity.dto.RegisterUserDto;
import com.ld.springsecurity.dto.ResendDto;
import com.ld.springsecurity.dto.VerifyUserDto;
import com.ld.springsecurity.model.Token;
import com.ld.springsecurity.model.TokenType;
import com.ld.springsecurity.model.User;
import com.ld.springsecurity.repo.TokenRepository;
import com.ld.springsecurity.repo.UserRepository;
import com.ld.springsecurity.response.LoginResponse;
import jakarta.mail.MessagingException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final EmailService emailService;

    private final TokenRepository tokenRepository;

    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, EmailService emailService, TokenRepository tokenRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.tokenRepository = tokenRepository;
        this.jwtService = jwtService;
    }

//    public User signup(RegisterUserDto input){
//        User user = new User(input.getUsername(), input.getEmail(), passwordEncoder.encode(input.getPassword()), input.getRole());
//        user.setVerificationCode(generateVerificationCode());
//        user.setVerificationCodeExpireAt(LocalDateTime.now().plusMinutes(15));
//        user.setEnabled(false);
//        sendVerificationEmail(user);
//        User savedUser = userRepository.save(user);
//        return savedUser;
//    }

    public void signup(RegisterUserDto input){
        if (userRepository.existsByEmail(input.getEmail())){
            throw new RuntimeException("This email has already been registered");
        }
        if (userRepository.existsByUsername(input.getUsername())){
            throw new RuntimeException("This username has already been taken");
        }
        User user = new User(input.getUsername(), input.getEmail(), passwordEncoder.encode(input.getPassword()), input.getRole());
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpireAt(LocalDateTime.now().plusMinutes(15));
        user.setEnabled(false);
        sendVerificationEmail(user);
        User savedUser = userRepository.save(user);
    }

    public LoginResponse auth(LoginUserDto input){
        User user = userRepository.findByUsername(input.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
//        if (!user.isEnabled()){
//            throw new RuntimeException("Account not verified. Please verify your account.");
//        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getUsername(),
                        input.getPassword()
                )
        );
        revokeAllTokenFromUser(user);
        String jwttoken = jwtService.generateToken(user);
        saveUserToken(user, jwttoken);
        return new LoginResponse(user.getUsername(), user.getEmail(), user.getRole(), jwttoken, jwtService.getJwtExpiration());
    }
    public void verifyUser(VerifyUserDto input){
        Optional<User> optionalUser = userRepository.findByUsername(input.getUsername());
        if (optionalUser.isPresent()){
            User user = optionalUser.get();
            if (user.getVerificationCodeExpireAt().isBefore(LocalDateTime.now())){
                throw new RuntimeException("Verification code has expired");
            }
            if (user.getVerificationCode().equals(input.getVerificationCode())){
                user.setEnabled(true);
                user.setVerificationCode(null);
                user.setVerificationCodeExpireAt(null);
                userRepository.save(user);
            }
            else {
                throw new RuntimeException("Invalid verification code");
            }
        }
        else {
            throw new RuntimeException("User not found");
        }
    }

    public void resendCode(ResendDto input){
        Optional<User> optionalUser = userRepository.findByUsername(input.getUsername());
        if (optionalUser.isPresent()){
            User user = optionalUser.get();
            if (user.isEnabled()){
                throw new RuntimeException("Account is already verified");
            }
            else {
                user.setVerificationCode(generateVerificationCode());
                user.setVerificationCodeExpireAt(LocalDateTime.now().plusMinutes(15));
                sendVerificationEmail(user);
                userRepository.save(user);
            }
        }
        else {
            throw new RuntimeException("User not found");
        }
    }

    public void sendVerificationEmail(User user){
        String subject = "Account Verification";
        String verificationCode = user.getVerificationCode();
        String htmlMessage = "<html lang=\"en\">"
                + "<body style=\"font-family: Arial, Helvetica, sans-serif;\">"
                + "<div style=\"background-color: #B76E79; padding: 30px;\">"
                + "<h2 style=\"color: #333\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 20px;\">Enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #e9e9e9; padding: 20px; border-radius: 5px;\">"
                + "<p style=\"font-size: 30px; font-weight: bold; color: #333;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";
        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e){
            e.printStackTrace();
        }
    }
    public String generateVerificationCode(){
        Random random = new Random();

        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    public void saveUserToken(User user, String jwttoken){
        Token token = Token.builder()
                .user(user)
                .token(jwttoken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    public void revokeAllTokenFromUser(User user){
        List<Token> validUserToken = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserToken.isEmpty()){
            return;
        }
        validUserToken.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserToken);
    }
}
