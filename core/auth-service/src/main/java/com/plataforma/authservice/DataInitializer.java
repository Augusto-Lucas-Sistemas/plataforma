package com.plataforma.authservice;

import com.plataforma.authservice.domain.model.User;
import com.plataforma.authservice.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Verifica se o usuário admin já existe
        if (userRepositoryPort.findByEmail("augustorenanss@gmail.com").isEmpty()) {
            log.info("Criando usuário 'admin' padrão...");

            User adminUser = new User();
            adminUser.setEmail("augustorenanss@gmail.com");
            adminUser.setPassword(passwordEncoder.encode("12345678"));
            adminUser.setRoles(Set.of("ROLE_ADMIN", "ROLE_USER"));

            userRepositoryPort.save(adminUser);
            log.info("Usuário 'admin' criado com sucesso.");
        } else {
            log.info("Usuário 'admin' já existe. Nenhuma ação necessária.");
        }
    }
}