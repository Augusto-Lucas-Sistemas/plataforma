package com.plataforma.gateway.security;

import com.plataforma.gateway.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtService jwtService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();
        String username;

        try {
            username = jwtService.extractUsername(authToken);
        } catch (Exception e) {
            return Mono.empty(); // Se houver erro ao extrair, considera falha na autenticação
        }

        if (username != null && jwtService.isTokenValid(authToken)) {
            // Se o token for válido, criamos um objeto de autenticação do Spring
            var auth = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER"))
            );
            return Mono.just(auth);
        } else {
            return Mono.empty();
        }
    }
}