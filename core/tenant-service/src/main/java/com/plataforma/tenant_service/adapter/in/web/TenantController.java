package com.plataforma.tenant_service.adapter.in.web;

import com.plataforma.tenant_service.adapter.in.web.dto.CreateTenantRequest;
import com.plataforma.tenant_service.domain.model.Tenant;
import com.plataforma.tenant_service.domain.port.in.TenantUseCase;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
public class TenantController {

    private static final Logger log = LoggerFactory.getLogger(TenantController.class);

    private final TenantUseCase tenantUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Tenant createTenant(@RequestBody CreateTenantRequest request) {
        // Log de INFO para marcar o início de uma operação de negócio importante.
        log.info("Recebida requisição para criar um novo tenant com nome: {}", request.name());

        Tenant tenant = Tenant.builder()
                .name(request.name())
                .subscribedModules(request.subscribedModules())
                .build();

        Tenant createdTenant = tenantUseCase.createTenant(tenant);
        log.info("Requisição para criar tenant finalizada. Tenant ID: {}", createdTenant.getId());
        return createdTenant;
    }

    @GetMapping
    public List<Tenant> getAllTenants() {
        log.info("Recebida requisição para listar todos os tenants.");
        List<Tenant> tenants = tenantUseCase.getAllTenants();
        log.info("Retornando {} tenants.", tenants.size());
        return tenants;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tenant> getTenantById(@PathVariable String id) {
        log.info("Recebida requisição para buscar tenant pelo ID: {}", id);

        return tenantUseCase.getTenantById(id)
                .map(tenant -> {
                    log.info("Tenant com ID {} encontrado.", id);
                    return ResponseEntity.ok(tenant);
                })
                .orElseGet(() -> {
                    // Log de WARN para uma situação comum, mas notável: um recurso não encontrado.
                    log.warn("Tenant com ID {} não foi encontrado.", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PutMapping("/{id}/modules")
    public Tenant addModuleToTenant(@PathVariable String id, @RequestBody Map<String, String> body) {
        String moduleName = body.get("moduleName");
        log.info("Recebida requisição para adicionar o módulo '{}' ao tenant com ID: {}", moduleName, id);

        Tenant updatedTenant = tenantUseCase.addModuleToTenant(id, moduleName);
        log.info("Módulo '{}' adicionado com sucesso ao tenant com ID: {}.", moduleName, id);
        return updatedTenant;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTenant(@PathVariable String id) {
        log.info("Recebida requisição para deletar o tenant com ID: {}", id);
        tenantUseCase.deleteTenant(id);
        log.info("Requisição para deletar tenant com ID {} finalizada.", id);
    }
}