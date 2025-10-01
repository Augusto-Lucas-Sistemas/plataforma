package com.plataforma.tenant_service.adapter.out.persistence;

import com.plataforma.tenant_service.domain.model.Tenant;
import com.plataforma.tenant_service.domain.port.out.TenantRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * ADAPTADOR DE SAÍDA
 * Implementa a porta de saída do domínio, conectando a lógica de negócio
 * à tecnologia de persistência (neste caso, Spring Data MongoDB).
 */
@Component // Anotação do Spring: indica que esta classe é um componente gerenciado
@RequiredArgsConstructor
public class TenantPersistenceAdapter implements TenantRepositoryPort {

    private final TenantMongoRepository mongoRepository;

    @Override
    public Tenant save(Tenant tenant) {
        return mongoRepository.save(tenant);
    }

    @Override
    public List<Tenant> findAll() {
        return mongoRepository.findAll();
    }

    @Override
    public Optional<Tenant> findById(String id) {
        return mongoRepository.findById(id);
    }

    @Override
    public void deleteById(String id) {
        mongoRepository.deleteById(id);
    }

    @Override
    public Optional<Tenant> findByName(String name) {
        return mongoRepository.findByName(name);
    }
}