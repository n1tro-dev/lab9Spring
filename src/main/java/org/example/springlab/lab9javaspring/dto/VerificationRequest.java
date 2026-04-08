package org.example.springlab.lab9javaspring.dto;

import lombok.Data;

@Data
public class VerificationRequest {
    private String username;
    private String code;
}
