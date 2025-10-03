package com.plataforma.authservice.service;

import com.plataforma.authservice.adapter.in.web.dto.UserResponse;
import com.plataforma.authservice.domain.model.User;
import com.plataforma.authservice.domain.port.in.GetAllUsersUseCase;
import com.plataforma.authservice.domain.port.in.LoginUseCase;
import com.plataforma.authservice.domain.port.in.RegisterUserCommand;
import com.plataforma.authservice.domain.port.in.RegisterUserUseCase;
import com.plataforma.authservice.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements RegisterUserUseCase, LoginUseCase, GetAllUsersUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public void register(RegisterUserCommand command) {
        userRepositoryPort.findByEmail(command.email()).ifPresent(user -> {
            throw new IllegalStateException("Usuário com este e-mail já existe.");
        });

        var newUser = new User();
        newUser.setEmail(command.email());
        newUser.setPassword(passwordEncoder.encode(command.password()));
        newUser.setRoles(Set.of("ROLE_USER"));

        userRepositoryPort.save(newUser);
    }

    @Override
    public String login(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return jwtService.generateToken(userDetails);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepositoryPort.findAll().stream()
                .map(user -> new UserResponse(user.getId(), user.getEmail(), user.getRoles()))
                .collect(Collectors.toList());
    }
}