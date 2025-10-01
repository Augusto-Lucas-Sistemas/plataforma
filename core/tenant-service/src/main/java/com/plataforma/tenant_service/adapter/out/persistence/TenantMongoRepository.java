package com.plataforma.tenant_service.adapter.out.persistence;

import com.plataforma.tenant_service.domain.model.Tenant;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Interface de Repositório do Spring Data MongoDB.
 * A implementação desta interface é gerada automaticamente pelo Spring.
 * Ela nos dá os métodos CRUD básicos (save, findById, findAll, etc.)
 * e nos permite criar consultas customizadas.
 */
@Repository
public interface TenantMongoRepository extends MongoRepository<Tenant, String> {

    // O Spring Data é inteligente o suficiente para criar a query
    // para este método apenas pelo nome "findByName".
    Optional<Tenant> findByName(String name);
}