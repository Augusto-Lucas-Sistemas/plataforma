package com.plataforma.authservice.domain.port.in;

public interface RegisterUserUseCase {
    void register(RegisterUserCommand command);
}