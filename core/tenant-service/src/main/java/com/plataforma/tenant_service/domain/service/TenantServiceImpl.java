package com.plataforma.tenant_service.domain.service;

import com.plataforma.tenant_service.domain.model.Tenant;
import com.plataforma.tenant_service.domain.port.in.TenantUseCase;
import com.plataforma.tenant_service.domain.port.out.TenantRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Implementação da lógica de negócio para os casos de uso de Tenant.
 * Esta classe orquestra as chamadas para as portas de saída (repositório).
 */
@Service // Anotação do Spring: indica que esta classe é um componente de serviço
@RequiredArgsConstructor // Lombok: cria um construtor com os campos 'final'
public class TenantServiceImpl implements TenantUseCase {

    // Dependemos da PORTA, não da implementação concreta do repositório!
    private final TenantRepositoryPort tenantRepositoryPort;

    @Override
    public Tenant createTenant(Tenant tenant) {
        // Exemplo de regra de negócio:
        // Sempre que um tenant é criado, ele começa como ATIVO e com data de criação.
        tenant.setStatus(Tenant.TenantStatus.ACTIVE);
        tenant.setCreatedAt(LocalDateTime.now());
        tenant.setUpdatedAt(LocalDateTime.now());
        if (tenant.getSubscribedModules() == null) {
            tenant.setSubscribedModules(new HashSet<>());
        }
        return tenantRepositoryPort.save(tenant);
    }

    @Override
    public List<Tenant> getAllTenants() {
        return tenantRepositoryPort.findAll();
    }

    @Override
    public Optional<Tenant> getTenantById(String id) {
        return tenantRepositoryPort.findById(id);
    }

    @Override
    public void deleteTenant(String id) {
        tenantRepositoryPort.deleteById(id);
    }

    @Override
    public Tenant addModuleToTenant(String tenantId, String moduleName) {
        // Lógica de negócio mais complexa:
        Tenant tenant = tenantRepositoryPort.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant não encontrado com o id: " + tenantId));

        Set<String> modules = tenant.getSubscribedModules();
        modules.add(moduleName);
        tenant.setSubscribedModules(modules);
        tenant.setUpdatedAt(LocalDateTime.now());

        return tenantRepositoryPort.save(tenant);
    }
}