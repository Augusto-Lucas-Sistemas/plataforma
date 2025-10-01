package com.plataforma.tenant_service.adapter.in.web;

import com.plataforma.tenant_service.adapter.in.web.dto.CreateTenantRequest;
import com.plataforma.tenant_service.domain.model.Tenant;
import com.plataforma.tenant_service.domain.port.in.TenantUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController // Indica que esta classe é um Controller REST
@RequestMapping("/api/v1/tenants") // Define o caminho base para todos os endpoints deste controller
@RequiredArgsConstructor
public class TenantController {

    // O Controller depende da PORTA de entrada, e não da implementação!
    private final TenantUseCase tenantUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Tenant createTenant(@RequestBody CreateTenantRequest request) {
        Tenant tenant = Tenant.builder()
                .name(request.name())
                .subscribedModules(request.subscribedModules())
                .build();
        return tenantUseCase.createTenant(tenant);
    }

    @GetMapping
    public List<Tenant> getAllTenants() {
        return tenantUseCase.getAllTenants();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tenant> getTenantById(@PathVariable String id) {
        return tenantUseCase.getTenantById(id)
                .map(ResponseEntity::ok) // Se o Optional contiver um valor, retorna 200 OK com o valor
                .orElse(ResponseEntity.notFound().build()); // Se o Optional estiver vazio, retorna 404 Not Found
    }

    @PutMapping("/{id}/modules")
    public Tenant addModuleToTenant(@PathVariable String id, @RequestBody Map<String, String> body) {
        String moduleName = body.get("moduleName");
        return tenantUseCase.addModuleToTenant(id, moduleName);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTenant(@PathVariable String id) {
        tenantUseCase.deleteTenant(id);
    }
}