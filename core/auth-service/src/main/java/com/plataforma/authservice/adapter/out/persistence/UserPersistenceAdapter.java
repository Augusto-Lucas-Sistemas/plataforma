package com.plataforma.authservice.adapter.out.persistence;

import com.plataforma.authservice.domain.model.User;
import com.plataforma.authservice.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Este é o Adaptador de Saída.
 * Ele implementa a porta de saída do nosso domínio (UserRepositoryPort) e
 * delega as chamadas para a tecnologia específica (neste caso, o UserMongoRepository).
 */
@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final UserMongoRepository userMongoRepository;

    @Override
    public User save(User user) {
        // Simplesmente delega a chamada para o repositório do Spring Data
        return userMongoRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        // Deleta a chamada para o método que o Spring Data criou para nós
        return userMongoRepository.findByEmail(email);
    }

    @Override
    public List<User> findAll() {
        // Deleta a chamada para o método findAll padrão do MongoRepository
        return userMongoRepository.findAll();
    }
}