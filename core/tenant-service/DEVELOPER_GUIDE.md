# Guia do Desenvolvedor: Modificando a Entidade `Tenant`

Este documento serve como um guia para desenvolvedores que precisam realizar modificações na entidade `Tenant` do `tenant-service`. Seguir estes passos garante que as alterações sejam feitas de forma segura, consistente e alinhada com a Arquitetura Hexagonal do projeto, minimizando o risco de erros.

## Cenário de Exemplo

Para ilustrar o processo, vamos adicionar um novo campo à nossa entidade:

- **Nome do Campo:** `contactEmail`
- **Tipo:** `String`
- **Objetivo:** Armazenar o e-mail de contato principal do cliente.

-----

## Passo a Passo para Adicionar um Novo Campo

O princípio da Arquitetura Hexagonal é trabalhar de "dentro para fora". Começamos pelo núcleo do nosso negócio (o domínio) e então ajustamos as camadas externas (adaptadores) que interagem com ele.

### Passo 1: Modificar a Entidade de Domínio (O Coração)

A primeira e mais importante alteração é no modelo de domínio. Esta é a fonte da verdade para o que um "Tenant" significa no nosso sistema.

1.  **Arquivo a ser modificado:** `src/main/java/com/plataforma/tenantservice/domain/model/Tenant.java`

2.  **Ação:** Adicione o novo campo à classe.

    ```java
    // ... outros campos ...
    private String name;
    private String contactEmail; // <-- NOVO CAMPO ADICIONADO
    private TenantStatus status;
    // ... resto da classe ...
    ```

3.  **Resultado:** Graças ao Lombok (`@Data`), os métodos `getContactEmail()` e `setContactEmail()` serão gerados automaticamente. A partir de agora, todo o nosso núcleo de negócio "sabe" que um Tenant pode ter um e-mail de contato.

### Passo 2: Atualizar o Contrato da API (DTOs)

Nunca exponha a entidade de domínio diretamente na API. Nós usamos DTOs (Data Transfer Objects) para definir o contrato com o mundo exterior. Se queremos que o cliente da API possa nos enviar este novo campo, precisamos atualizar o DTO de requisição.

1.  **Arquivo a ser modificado:** `src/main/java/com/plataforma/tenantservice/adapter/in/web/dto/CreateTenantRequest.java`

2.  **Ação:** Adicione o novo campo ao `record`.

    ```java
    public record CreateTenantRequest(
        String name,
        String contactEmail, // <-- NOVO CAMPO ADICIONADO
        Set<String> subscribedModules
    ) {}
    ```

3.  **Nota sobre Respostas:** Atualmente, nossas rotas `GET` retornam o objeto `Tenant` completo, então o novo campo `contactEmail` já aparecerá nas respostas automaticamente. Para um controle mais fino no futuro, poderíamos criar um DTO de resposta (ex: `TenantResponse.java`).

### Passo 3: Ajustar o Adaptador de Entrada (Controller)

O Controller é a ponte entre o mundo HTTP (com seus DTOs) e nosso domínio (com suas entidades). Precisamos ensiná-lo a mapear o novo campo do DTO para a entidade.

1.  **Arquivo a ser modificado:** `src/main/java/com/plataforma/tenantservice/adapter/in/web/TenantController.java`

2.  **Ação:** Na classe `createTenant`, atualize o `builder` para incluir o novo campo.

    ```java
    // Dentro do método createTenant(...)

    Tenant tenant = Tenant.builder()
            .name(request.name())
            .contactEmail(request.contactEmail()) // <-- LINHA ADICIONADA
            .subscribedModules(request.subscribedModules())
            .build();
    return tenantUseCase.createTenant(tenant);
    ```

### Passo 4: Lógica de Negócio e Portas (Nenhuma Alteração Necessária\!)

Esta é a grande vantagem da nossa arquitetura. Como nossas portas (`TenantUseCase`, `TenantRepositoryPort`) e serviços (`TenantServiceImpl`) já trabalham com o objeto `Tenant` completo, o novo campo `contactEmail` simplesmente "passa" por eles dentro do objeto.

**Nenhuma modificação é necessária nesta camada.** Isso demonstra o baixo acoplamento e a manutenibilidade do nosso design.

### Passo 5: Camada de Persistência (Nenhuma Alteração Necessária\!)

Outra grande vantagem. Como estamos usando MongoDB (um banco de dados NoSQL sem esquema fixo) e Spring Data, o novo campo `contactEmail` será persistido automaticamente no documento do banco de dados na próxima vez que um tenant for salvo ou atualizado.

**Nenhuma alteração de código é necessária** no `TenantPersistenceAdapter` ou no `TenantMongoRepository`.

-----

## Checklist de Verificação e Teste

Após realizar as alterações, siga este checklist para garantir que tudo funciona como esperado.

#### Arquivos Modificados (Resumo)

- [x] `domain/model/Tenant.java`
- [x] `adapter/in/web/dto/CreateTenantRequest.java`
- [x] `adapter/in/web/TenantController.java`

#### Como Testar

1.  **Reconstrua a imagem Docker** com as novas alterações. Na raiz do projeto, rode:
    ```bash
    docker-compose up --build
    ```
2.  **Abra o Postman** e vá para a requisição `Criar Novo Tenant`.
3.  **Modifique o corpo (Body) da requisição** para incluir o novo campo:
    ```json
    {
        "name": "Consultório Dr. House",
        "contactEmail": "contato@housemd.com",
        "subscribedModules": [
            "mod-diagnostico"
        ]
    }
    ```
4.  **Envie a requisição.** Verifique se a resposta (`201 Created`) contém o campo `contactEmail` com o valor que você enviou.
5.  **Use a requisição `Buscar Tenant por ID`** (com o ID da resposta anterior) para confirmar que o dado foi realmente salvo e é retornado corretamente.