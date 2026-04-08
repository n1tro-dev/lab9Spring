package org.example.springlab.lab9javaspring.controller;

import lombok.RequiredArgsConstructor;
import org.example.springlab.lab9javaspring.dto.LoginRequest;
import org.example.springlab.lab9javaspring.dto.VerificationRequest;
import org.example.springlab.lab9javaspring.entity.User;
import org.example.springlab.lab9javaspring.repository.UserRepository;
import org.example.springlab.lab9javaspring.service.EmailService;
import org.example.springlab.lab9javaspring.service.JwtService;
import org.example.springlab.lab9javaspring.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        if (userRepository.findByUserName(user.getUserName()).isPresent()) {
            return ResponseEntity.badRequest().body("Пользователь уже существует");
        }

        userService.register(user);
        return ResponseEntity.ok("Регистрация прошла успешно! Письмо отправлено на почту.");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {

        return userRepository.findByUserName(loginRequest.getUsername())
                .map(user -> {
                    if (!user.isEnabled()){
                        return ResponseEntity.status(403).body("Аккаунт не подтвержден. Введите код из письма.");
                    }

                    if (passwordEncoder.matches(loginRequest.getPassword(),user.getPassword())) {
                        String token = jwtService.generateToken(user.getUserName());
                        return ResponseEntity.ok("Авторизация успешна! Bearer: " + token);
                    } else {
                        return ResponseEntity.status(401).body("Неверный пароль");
                    }
                })
                .orElse(ResponseEntity.status(404).body("Пользователь не найден"));
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyCode(@RequestBody VerificationRequest request){
        return userRepository.findByUserName(request.getUsername())
                .map(user -> {
                    if(user.getVerificationCode() != null && user.getVerificationCode().equals(request.getCode())){
                        user.setEnabled(true);
                        user.setVerificationCode(null);
                        userRepository.save(user);
                        return ResponseEntity.ok("Аккаунт активирован!");
                    }
                    return ResponseEntity.badRequest().body("Неверный или просроченный код");
                }).orElse(ResponseEntity.status(404).body("Пользователь не найден"));
    }
}
