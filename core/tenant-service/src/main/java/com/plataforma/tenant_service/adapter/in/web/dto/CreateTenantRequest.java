package com.plataforma.tenant_service.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.util.Set;

/**
 * DTO para a criação de um novo Tenant.
 * Contém apenas os dados necessários que o cliente deve enviar.
 */
public record CreateTenantRequest(

        @NotBlank(message = "Campo 'name' deve ser preenchido")
        @Length(min = 3, max = 40, message = "Campo 'name' deve ter entre 3 e 30 caracteres")
        String name,

        @NotNull(message = "Campo 'subscribedModules' deve ser preenchido")
        @NotEmpty(message = "Campo 'subscribedModules' deve ter pelo menos um item")
        Set<String> subscribedModules
) {}