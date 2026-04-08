package org.example.springlab.lab9javaspring.repository;

import org.example.springlab.lab9javaspring.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUserName(String userName);
}
