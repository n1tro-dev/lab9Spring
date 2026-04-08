package org.example.springlab.lab9javaspring.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Data
public class User {
    @Id
    private String id;
    private String userName;
    private String email;
    private String password;

    private String verificationCode;
    private boolean enabled = false;
}
