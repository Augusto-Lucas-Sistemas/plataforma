# Core: Tenant Service

Microserviço responsável pelo gerenciamento de **Tenants** (clientes) da plataforma. Ele centraliza todas as regras de negócio e operações relacionadas à criação, consulta, atualização e exclusão de tenants, bem como o controle dos módulos que cada um tem acesso.

## 1\. Regras de Negócio

Esta seção detalha os conceitos e regras que governam o domínio do `Tenant Service`.

### 1.1. O Conceito de "Tenant"

Um **Tenant** representa um cliente individual da plataforma. Cada tenant é uma entidade isolada com seus próprios usuários, dados e configurações. Exemplos de tenants seriam "Oficina do Zé" ou "Clínica Sorriso".

### 1.2. Atributos de um Tenant

- `id` (String): Identificador único (MongoDB Object ID).
- `name` (String): Nome comercial do cliente.
- `contactEmail` (String): E-mail de contato principal do cliente.
- `status` (Enum): A situação atual do tenant na plataforma.
- `subscribedModules` (Set\<String\>): Lista dos identificadores dos módulos que o tenant contratou.
- `createdAt` (LocalDateTime): Data e hora do registro.
- `updatedAt` (LocalDateTime): Data e hora da última modificação.

### 1.3. Status do Tenant

- **ACTIVE**: O tenant está ativo e pode utilizar os módulos contratados.
- **INACTIVE**: O tenant foi desativado e não pode mais acessar a plataforma.
- **PENDING\_PAYMENT**: O tenant está com pagamentos pendentes.
- **SUSPENDED**: O tenant foi suspenso por razões administrativas.

## 2\. Detalhes Técnicos

### 2.1. Arquitetura

O `tenant-service` foi construído utilizando a **Arquitetura Hexagonal (Portas e Adaptadores)**. Esta abordagem isola o núcleo de regras de negócio de detalhes externos.

- **Domínio (`domain`):** Contém a lógica de negócio pura (entidades, casos de uso, portas).
- **Adaptadores (`adapter`):** Conectam o domínio com o mundo exterior (Controladores REST, Adaptadores de Persistência com MongoDB).

### 2.2. Tecnologias Utilizadas

- **Java 21** (LTS)
- **Spring Boot 3.2.5**
- **Spring Cloud 2023.0.1**
- **Spring Cloud Config Client**: Para consumir configurações de um servidor centralizado.
- **Spring Cloud Netflix Eureka Client**: Para registro e descoberta de serviço.
- **Spring Web**: Para a criação dos endpoints REST.
- **Spring Data MongoDB**: Para a integração com o banco de dados.
- **SpringDoc OpenAPI (Swagger)**: Para geração de documentação de API interativa.
- **Maven**: Gerenciamento de dependências e build.
- **Lombok**: Redução de código boilerplate.
- **Docker & Docker Compose**: Para conteinerização e orquestração.

### 2.3. API Endpoints e Documentação (Swagger)

A API está disponível sob o caminho base `/api/v1/tenants`. A documentação interativa gerada pelo Swagger é a forma recomendada para explorar e testar os endpoints individualmente.

- **Swagger UI (Interface Gráfica):** [http://localhost:8081/swagger-ui.html](https://www.google.com/search?q=http://localhost:8081/swagger-ui.html)
- **Definição OpenAPI (JSON):** [http://localhost:8081/v3/api-docs](https://www.google.com/search?q=http://localhost:8081/v3/api-docs)

| Método | Endpoint                    | Descrição                                         |
| :----- | :-------------------------- | :------------------------------------------------ |
| `POST` | `/`                         | Cria um novo tenant.                              |
| `GET`  | `/`                         | Retorna uma lista de todos os tenants.            |
| `GET`  | `/{id}`                     | Busca um tenant específico pelo seu ID.           |
| `PUT`  | `/{id}/modules`             | Adiciona um novo módulo a um tenant existente.     |
| `DELETE` | `/{id}`                     | Deleta um tenant pelo seu ID.                      |

### 2.4. Testes com Postman/Insomnia

Para testes de integração, os endpoints deste serviço estão incluídos na **coleção geral do Postman/Insomnia** da plataforma, localizada na raiz do projeto. Dentro da coleção, as requisições para o `tenant-service` estão organizadas em sua própria pasta.

### 2.5. Integração com o Config Server

Este serviço é um **Config Client**, o que significa que ele busca todas as suas configurações no `config-server` centralizado durante a inicialização.

Essa funcionalidade é habilitada por dois componentes principais:

1.  **Dependência Maven:** O `pom.xml` inclui `spring-cloud-starter-config`.
2.  **Configuração de Bootstrap:** O arquivo `application.yml` local contém as instruções para encontrar o Config Server via Eureka:
    ```yaml
    spring:
      config:
        import: "configserver:"
      cloud:
        config:
          discovery:
            enabled: true
            service-id: config-server
    ```

Isso garante que propriedades como a porta do servidor e a URI do banco de dados sejam gerenciadas externamente no repositório Git `plataforma-config`.

### 2.6. Integração com o Discovery Server (Eureka)

Este serviço também é um **Discovery Client**, registrando-se ativamente no `discovery-server` (Eureka) ao iniciar. Isso permite que outros serviços da plataforma o encontrem dinamicamente.

Essa funcionalidade é habilitada por:

1.  **Dependência Maven:** `spring-cloud-starter-netflix-eureka-client`.
2.  **Anotação:** A classe principal é anotada com `@EnableDiscoveryClient`.
3.  **Configuração:** O `application.yml` local contém as propriedades que apontam para o Eureka.

## 3\. Como Executar

### 3.1. Como Parte da Plataforma (Modo Padrão com Docker)

A forma principal de execução é via Docker Compose, que orquestra todos os serviços.

1.  Navegue até a **pasta raiz do projeto**.
2.  Execute o comando:
    ```bash
    docker-compose up --build
    ```
3.  O serviço buscará suas configurações, subirá na porta correta (ex: `8081`) e se registrará no Discovery Server.

### 3.2. De Forma Isolada (Standalone - para Debug)

Para desenvolvimento e depuração focados neste serviço, você pode executá-lo diretamente pela sua IDE.

1.  Abra o projeto `plataforma` na sua IDE (ex: IntelliJ).
2.  **Importante:** Este serviço possui dependências externas. Antes de prosseguir, certifique-se de que elas estejam rodando. Você pode iniciá-las com o Docker Compose:
    ```bash
    # Na raiz do projeto, inicie apenas as dependências
    docker-compose up -d mongodb discovery-server config-server
    ```
3.  Com as dependências no ar, encontre e execute a classe principal `TenantServiceApplication.java`.
4.  O serviço buscará suas configurações no `config-server` e subirá na porta configurada (ex: `8081`).