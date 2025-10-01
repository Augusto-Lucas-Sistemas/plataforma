package com.plataforma.tenant_service.adapter.in.web.dto;

import java.util.Set;

/**
 * DTO para a criação de um novo Tenant.
 * Contém apenas os dados necessários que o cliente deve enviar.
 */
public record CreateTenantRequest(
        String name,
        Set<String> subscribedModules
) {}