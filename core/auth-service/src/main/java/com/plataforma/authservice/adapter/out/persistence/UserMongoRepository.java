package com.plataforma.authservice.adapter.out.persistence;

import com.plataforma.authservice.domain.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserMongoRepository extends MongoRepository<User, String> {

    // O Spring Data irá automaticamente criar a implementação deste método
    // com base no nome "findByEmail".
    Optional<User> findByEmail(String email);
}