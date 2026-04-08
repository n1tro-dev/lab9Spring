package org.example.springlab.lab9javaspring.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
