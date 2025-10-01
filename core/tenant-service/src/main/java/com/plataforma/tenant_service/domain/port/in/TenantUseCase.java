package com.plataforma.tenant_service.domain.port.in;

import com.plataforma.tenant_service.domain.model.Tenant;

import java.util.List;
import java.util.Optional;

/**
 * Porta de Entrada (Driving Port) para os casos de uso de Tenant.
 *
 * Define a API pública do nosso domínio. Qualquer ator externo (como um Controller Web)
 * deve interagir com o domínio através desta porta.
 */
public interface TenantUseCase {

    Tenant createTenant(Tenant tenant);

    List<Tenant> getAllTenants();

    Optional<Tenant> getTenantById(String id);

    void deleteTenant(String id);

    Tenant addModuleToTenant(String tenantId, String moduleName);
}