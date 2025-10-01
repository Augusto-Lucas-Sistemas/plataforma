package com.plataforma.tenant_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Representa um Tenant (cliente) da plataforma.
 * Esta é uma entidade de domínio pura, descrevendo os dados e o estado de um Tenant.
 */
@Data // Anotação do Lombok: cria getters, setters, toString, equals, hashCode
@Builder // Anotação do Lombok: Padrão de projeto Builder para criar objetos
@NoArgsConstructor // Lombok: Construtor sem argumentos
@AllArgsConstructor // Lombok: Construtor com todos os argumentos
@Document(collection = "tenants") // Anotação do Spring Data: Mapeia esta classe para uma coleção no MongoDB
public class Tenant {

    @Id
    private String id; // Chave primária no MongoDB é uma String

    private String name; // Nome do cliente, ex: "Oficina do Zé"

    private TenantStatus status; // Status do cliente (ATIVO, INATIVO, etc.)

    private Set<String> subscribedModules; // Lista de módulos que este cliente assina, ex: ["mod-oficina"]

    private LocalDateTime createdAt; // Data de criação do registro

    private LocalDateTime updatedAt; // Data da última atualização

    public enum TenantStatus {
        ACTIVE,
        INACTIVE,
        PENDING_PAYMENT,
        SUSPENDED
    }
}