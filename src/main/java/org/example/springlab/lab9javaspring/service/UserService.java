package org.example.springlab.lab9javaspring.service;

import org.example.springlab.lab9javaspring.entity.User;
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

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public void register(User user) {
        String code = String.format("%06d", new Random().nextInt(1000000));

        user.setVerificationCode(code);
        user.setEnabled(false);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        StopWatch timer = new StopWatch();
        timer.start("таймер вне метода");

        emailService.sendSimpleEmail(user.getEmail(), "Код подтверждения", "Ваш код: " + code);

        timer.stop();
        System.out.println(timer.prettyPrint());
    }
}
