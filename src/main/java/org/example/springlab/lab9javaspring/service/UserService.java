package org.example.springlab.lab9javaspring.service;

import lombok.extern.slf4j.Slf4j;
import org.example.springlab.lab9javaspring.entity.AuditLog;
import org.example.springlab.lab9javaspring.entity.User;
import org.example.springlab.lab9javaspring.repository.AuditLogRepository;
import org.example.springlab.lab9javaspring.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
public class UserService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final AuditLogRepository auditLogRepository;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, EmailService emailService, AuditLogRepository auditLogRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.auditLogRepository = auditLogRepository;
    }

    public void register(User user) {

        log.info("Запуск процесса регистрации для пользователя: {}", user.getUserName());

        try {
            String code = String.format("%06d", new Random().nextInt(1000000));

            user.setVerificationCode(code);
            user.setEnabled(false);

            log.debug("Генерация хеша пароля для {}", user.getUserName());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);

            AuditLog auditLog = new AuditLog();
            auditLog.setUserName(user.getUserName());
            auditLog.setAction("User registered with email: " + user.getEmail());
            auditLogRepository.save(auditLog);

            log.info("Отправка кода подтверждения на почту: {}", user.getEmail());
            emailService.sendSimpleEmail(user.getEmail(), "Код подтверждения", "Ваш код: " + code);

            log.info("Пользователь {} успешно сохранен и ожидает подтверждения", user.getUserName());

        } catch (Exception e){
            log.error("Критическая ошибка при регистрации пользователя {}: {}", user.getUserName(), e.getMessage());
            throw e;
        }


    }
}
