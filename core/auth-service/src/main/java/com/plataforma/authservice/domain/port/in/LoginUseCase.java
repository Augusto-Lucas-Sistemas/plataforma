package com.plataforma.authservice.domain.port.in;

public interface LoginUseCase {
    String login(String email, String password);
}