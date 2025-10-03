package com.plataforma.authservice.adapter.in.web;

import com.plataforma.authservice.adapter.in.web.dto.AuthRequest;
import com.plataforma.authservice.adapter.in.web.dto.UserResponse;
import com.plataforma.authservice.domain.port.in.GetAllUsersUseCase;
import com.plataforma.authservice.domain.port.in.LoginUseCase;
import com.plataforma.authservice.domain.port.in.RegisterUserCommand;
import com.plataforma.authservice.domain.port.in.RegisterUserUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    // AGORA O CONTROLLER DEPENDE APENAS DAS INTERFACES (PORTAS DE ENTRADA)
    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;
    private final GetAllUsersUseCase getAllUsersUseCase;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterUserCommand command) {
        try {
            registerUserUseCase.register(command);
            return ResponseEntity.status(HttpStatus.CREATED).body("Usuário criado com sucesso.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest request) {
        try {
            String token = loginUseCase.login(request.email(), request.password());
            return ResponseEntity.ok(token);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou senha inválidos.");
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = getAllUsersUseCase.getAllUsers();
        return ResponseEntity.ok(users);
    }
}