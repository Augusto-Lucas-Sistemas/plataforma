package com.plataforma.tenant_service.domain.service;

import com.plataforma.tenant_service.domain.model.Tenant;
import com.plataforma.tenant_service.domain.port.in.TenantUseCase;
import com.plataforma.tenant_service.domain.port.out.TenantRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantUseCase {

    private static final Logger log = LoggerFactory.getLogger(TenantServiceImpl.class);

    private final TenantRepositoryPort tenantRepositoryPort;

    @Override
    public Tenant createTenant(Tenant tenant) {
        log.debug("Executando lógica de negócio para criar tenant: {}", tenant.getName());

        // Validação de negócio
        tenantRepositoryPort.findByName(tenant.getName()).ifPresent(existingTenant -> {
            log.error("Tentativa de criar tenant com nome duplicado: {}", tenant.getName());
            throw new IllegalArgumentException("Já existe um tenant com o nome: " + tenant.getName());
        });

        // Lógica de negócio
        tenant.setStatus(Tenant.TenantStatus.ACTIVE);
        tenant.setCreatedAt(LocalDateTime.now());
        tenant.setUpdatedAt(LocalDateTime.now());
        if (tenant.getSubscribedModules() == null) {
            tenant.setSubscribedModules(new HashSet<>());
        }

        log.debug("Tenant pré-processado, pronto para salvar. Chamando a porta de persistência.");
        Tenant savedTenant = tenantRepositoryPort.save(tenant);

        log.info("Tenant '{}' (ID: {}) persistido com sucesso no banco de dados.", savedTenant.getName(), savedTenant.getId());
        return savedTenant;
    }

    @Override
    public List<Tenant> getAllTenants() {
        log.debug("Chamando a porta de persistência para buscar todos os tenants.");
        return tenantRepositoryPort.findAll();
    }

    @Override
    public Optional<Tenant> getTenantById(String id) {
        log.debug("Chamando a porta de persistência para buscar tenant pelo ID: {}", id);
        return tenantRepositoryPort.findById(id);
    }

    @Override
    public void deleteTenant(String id) {
        log.warn("Iniciando operação de deleção para o tenant com ID: {}. Esta é uma ação destrutiva.", id);

        // Verificação de existência antes de deletar para um log mais preciso
        if (tenantRepositoryPort.findById(id).isEmpty()) {
            log.warn("Tentativa de deletar um tenant que não existe. ID: {}", id);
            // Poderíamos lançar um erro aqui, mas por ora apenas logamos e permitimos que a operação continue (será idempotente).
        }

        tenantRepositoryPort.deleteById(id);
        log.info("Tenant com ID: {} deletado com sucesso da base de dados.", id);
    }

    @Override
    public Tenant addModuleToTenant(String tenantId, String moduleName) {
        log.debug("Iniciando lógica para adicionar módulo '{}' ao tenant '{}'", moduleName, tenantId);

        Tenant tenant = tenantRepositoryPort.findById(tenantId)
                .orElseThrow(() -> {
                    // Logamos o erro antes de lançar a exceção.
                    log.error("Falha ao tentar adicionar módulo: Tenant com ID '{}' não foi encontrado.", tenantId);
                    return new RuntimeException("Tenant não encontrado com o id: " + tenantId);
                });

        Set<String> modules = tenant.getSubscribedModules();
        if (modules.contains(moduleName)) {
            // Log de WARN para uma operação redundante. Não é um erro, mas é bom saber.
            log.warn("Módulo '{}' já está inscrito para o tenant '{}'. Nenhuma alteração será feita.", moduleName, tenant.getName());
            return tenant; // Retorna o objeto sem modificação.
        }

        modules.add(moduleName);
        tenant.setSubscribedModules(modules);
        tenant.setUpdatedAt(LocalDateTime.now());

        log.debug("Tenant '{}' atualizado em memória. Chamando a porta de persistência.", tenant.getName());
        Tenant updatedTenant = tenantRepositoryPort.save(tenant);

        log.info("Módulo '{}' adicionado com sucesso ao tenant '{}' (ID: {})", moduleName, updatedTenant.getName(), updatedTenant.getId());
        return updatedTenant;
    }
}