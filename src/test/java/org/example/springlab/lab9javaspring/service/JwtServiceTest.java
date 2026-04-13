package org.example.springlab.lab9javaspring.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    @Test
    void shouldGenerateValidToken() {

        JwtService jwtService = new JwtService();
        String username = "Nikita";

        String token = jwtService.generateToken(username);

        assertNotNull(token,"Не должен быть null");
        assertFalse(token.isEmpty(), "Не должен быть пустым");

        String extractedUsername = jwtService.extractUsername(token);
        assertEquals(username, extractedUsername, "Имя пользователя должно совпадать тем что в токене");
    }

}