package org.example.springlab.lab9javaspring.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.springlab.lab9javaspring.dto.LoginRequest;
import org.example.springlab.lab9javaspring.dto.VerificationRequest;
import org.example.springlab.lab9javaspring.entity.User;
import org.example.springlab.lab9javaspring.exception.GlobalExceptionHandler;
import org.example.springlab.lab9javaspring.repository.UserRepository;
import org.example.springlab.lab9javaspring.service.EmailService;
import org.example.springlab.lab9javaspring.service.JwtService;
import org.example.springlab.lab9javaspring.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Slf4j
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
        log.info("Получен запрос на регистрацию пользователя: {}", user.getUserName());
        if (userRepository.findByUserName(user.getUserName()).isPresent()) {
            log.warn("Попытка регистрации дубликата пользователя: {}", user.getUserName());
            return ResponseEntity.badRequest().body("Пользователь уже существует");
        }

        userService.register(user);
        log.info("Пользователь {} успешно зарегистрирован", user.getUserName());
        return ResponseEntity.ok("Регистрация прошла успешно! Письмо отправлено на почту.");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        log.info("Попытка входа пользователя: {}", loginRequest.getUsername());

        return userRepository.findByUserName(loginRequest.getUsername())
                .map(user -> {
                    if (!user.isEnabled()){
                        log.warn("Пользователь {} пытался войти без подтверждения email", user.getUserName());
                        return ResponseEntity.status(403).body("Аккаунт не подтвержден. Введите код из письма.");
                    }

                    if (passwordEncoder.matches(loginRequest.getPassword(),user.getPassword())) {
                        String token = jwtService.generateToken(user.getUserName());
                        log.info("Пользователь {} успешно авторизован", user.getUserName());
                        return ResponseEntity.ok("Авторизация успешна! Bearer: " + token);
                    } else {
                        log.warn("Неверный пароль для пользователя: {}", user.getUserName());
                        return ResponseEntity.status(401).body("Неверный пароль");
                    }
                })
                .orElseThrow(() -> {
                        log.error("Пользователь не найден при попытке входа: {}", loginRequest.getUsername());
                        return new UsernameNotFoundException("Пользователь не найден");
                });
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyCode(@RequestBody VerificationRequest request){
        log.info("Запрос на верификацию кода для пользователя: {}", request.getUsername());

        return userRepository.findByUserName(request.getUsername())
                .map(user -> {
                    if(user.getVerificationCode() != null && user.getVerificationCode().equals(request.getCode())){
                        user.setEnabled(true);
                        user.setVerificationCode(null);
                        userRepository.save(user);
                        log.info("Аккаунт пользователя {} успешно активирован", user.getUserName());
                        return ResponseEntity.ok("Аккаунт активирован!");
                    }
                    log.warn("Введен неверный код активации для пользователя: {}", user.getUserName());
                    return ResponseEntity.badRequest().body("Неверный или просроченный код");
                }).orElseThrow(() -> {
                    log.error("Пользователь {} не найден при верификации", request.getUsername());
                    return new UsernameNotFoundException("Пользователь не найден");
                });


    }
}
