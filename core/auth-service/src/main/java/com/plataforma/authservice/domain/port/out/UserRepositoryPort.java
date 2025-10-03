package com.plataforma.authservice.domain.port.out;

import com.plataforma.authservice.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {
    User save(User user);

    Optional<User> findByEmail(String email);

    List<User> findAll();
}