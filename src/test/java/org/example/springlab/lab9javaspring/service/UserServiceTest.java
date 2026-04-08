package org.example.springlab.lab9javaspring.service;

import org.example.springlab.lab9javaspring.entity.User;
import org.example.springlab.lab9javaspring.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.Objects;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository repository;
    @Mock
    private EmailService emailService;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService service;

    @Test
    void register_ShouldSaveUser() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setPassword("password");

        when((passwordEncoder.encode(anyString()))).thenReturn("hashedPassword");
        service.register(user);

        verify(repository, times(1)).save(any(User.class));
        verify(emailService, times(1)).sendSimpleEmail(eq("test@mail.ru"),anyString(),anyString());
    }


}