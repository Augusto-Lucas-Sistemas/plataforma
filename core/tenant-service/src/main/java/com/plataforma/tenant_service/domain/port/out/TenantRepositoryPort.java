package com.plataforma.tenant_service.domain.port.out;

import com.plataforma.tenant_service.domain.model.Tenant;

import java.util.List;
import java.util.Optional;

/**
 * Porta de Saída (Driven Port) para operações de persistência de Tenants.
 *
 * Esta interface define o CONTRATO que o domínio precisa para persistir dados,
 * sem se acoplar a nenhuma tecnologia de banco de dados específica.
 */
public interface TenantRepositoryPort {

    /**
     * Salva ou atualiza um Tenant.
     *
     * @param tenant o Tenant a ser salvo.
     * @return o Tenant salvo (geralmente com o ID preenchido).
     */
    Tenant save(Tenant tenant);

    /**
     * Busca todos os Tenants.
     *
     * @return uma lista com todos os Tenants.
     */
    List<Tenant> findAll();

    /**
     * Busca um Tenant pelo seu ID.
     *
     * @param id o ID do Tenant.
     * @return um Optional contendo o Tenant se encontrado, ou vazio caso contrário.
     */
    Optional<Tenant> findById(String id);

    /**
     * Deleta um Tenant pelo seu ID.
     *
     * @param id o ID do Tenant a ser deletado.
     */
    void deleteById(String id);

    /**
     * Busca um Tenant pelo nome.
     *
     * @param name o nome do Tenant.
     * @return um Optional contendo o Tenant se encontrado.
     */
    Optional<Tenant> findByName(String name);
}