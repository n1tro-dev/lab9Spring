package org.example.springlab.lab9javaspring.service;

import org.example.springlab.lab9javaspring.entity.AuditLog;
import org.example.springlab.lab9javaspring.entity.User;
import org.example.springlab.lab9javaspring.repository.AuditLogRepository;
import org.example.springlab.lab9javaspring.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.Random;

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
        String code = String.format("%06d", new Random().nextInt(1000000));

        user.setVerificationCode(code);
        user.setEnabled(false);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        AuditLog log = new AuditLog();
        log.setUserName(user.getUserName());
        log.setAction("User registered with email: " + user.getEmail());
        auditLogRepository.save(log);

        emailService.sendSimpleEmail(user.getEmail(), "Код подтверждения", "Ваш код: " + code);

    }
}
