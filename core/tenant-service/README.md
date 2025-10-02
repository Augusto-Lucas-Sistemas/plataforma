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

O `tenant-service` foi construído utilizando a **Arquitetura Hexagonal (Portas e Adaptadores)**. Esta abordagem isola o núcleo de regras de negócio de detalhes externos (Controladores REST, Adaptadores de Persistência com MongoDB).

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

### 2.3. Acesso à API e Testes

**Importante:** Todo o acesso externo a este serviço deve ser feito através do **API Gateway**, que escuta na porta `8080`.

A API está disponível sob o caminho base `/api/v1/tenants`.

| Método | Endpoint                    | Exemplo de URL via Gateway |
| :----- | :-------------------------- | :--- |
| `POST` | `/`                         | `http://localhost:8080/api/v1/tenants` |
| `GET`  | `/`                         | `http://localhost:8080/api/v1/tenants` |
| `GET`  | `/{id}`                     | `http://localhost:8080/api/v1/tenants/{id}` |
| `PUT`  | `/{id}/modules`             | `http://localhost:8080/api/v1/tenants/{id}/modules`|
| `DELETE` | `/{id}`                     | `http://localhost:8080/api/v1/tenants/{id}` |

#### Swagger UI

A documentação interativa da API pode ser acessada através do Gateway:

- **URL:** [**http://localhost:8080/api/v1/tenants/swagger-ui.html**](https://www.google.com/search?q=http://localhost:8080/api/v1/tenants/swagger-ui.html)

#### Postman/Insomnia

Para testes de integração, utilize a coleção `postman_collection.json` na raiz do projeto. A variável `baseUrl` já está configurada para `http://localhost:8080`, então as requisições na pasta "Tenant Service" funcionarão corretamente.

### 2.4. Integração com o Config Server

Este serviço é um **Config Client**, buscando suas configurações no `config-server` durante a inicialização. Isso é habilitado pela dependência `spring-cloud-starter-config` e pelas propriedades `spring.config.import` no `application.yml`.

### 2.5. Integração com o Discovery Server (Eureka)

Este serviço também é um **Discovery Client**, registrando-se ativamente no `discovery-server` (Eureka) ao iniciar, o que permite que o API Gateway o encontre dinamicamente.

## 3\. Como Executar

### 3.1. Como Parte da Plataforma (Modo Padrão com Docker)

A forma principal de execução é via Docker Compose.

1.  Navegue até a **pasta raiz do projeto**.
2.  Execute o comando:
    ```bash
    docker-compose up --build
    ```
3.  O serviço não estará diretamente acessível. O acesso deve ser feito através do **API Gateway na porta `8080`**.

### 3.2. De Forma Isolada (Standalone - para Debug)

Para desenvolvimento e depuração focados neste serviço, você pode executá-lo diretamente pela sua IDE.

1.  Abra o projeto `plataforma` na sua IDE (ex: IntelliJ).
2.  **Importante:** Este serviço possui dependências externas. Inicie-as primeiro com o Docker Compose:
    ```bash
    docker-compose up -d mongodb discovery-server config-server
    ```
3.  Com as dependências no ar, execute a classe principal `TenantServiceApplication.java`.
4.  A aplicação iniciará na porta `8081` (conforme configuração vinda do Config Server). Para testar os endpoints, você precisará iniciar também o `gateway` ou fazer chamadas diretamente para `http://localhost:8081`.