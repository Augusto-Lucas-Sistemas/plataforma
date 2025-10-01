# Tenant Service

Microserviço responsável pelo gerenciamento de **Tenants** (clientes) da plataforma. Ele centraliza todas as regras de negócio e operações relacionadas à criação, consulta, atualização e exclusão de tenants, bem como o controle dos módulos que cada um tem acesso.

## 1\. Regras de Negócio

Esta seção detalha os conceitos e regras que governam o domínio do `Tenant Service`.

### 1.1. O Conceito de "Tenant"

Um **Tenant** representa um cliente individual da plataforma. Cada tenant é uma entidade isolada com seus próprios usuários, dados e configurações. Exemplos de tenants seriam "Oficina do Zé" ou "Clínica Sorriso".

### 1.2. Atributos de um Tenant

Um tenant é definido pelos seguintes atributos:

- `id` (String): Identificador único gerado automaticamente pelo sistema (MongoDB Object ID).
- `name` (String): Nome comercial do cliente.
- `contactEmail` (String): E-mail de contato principal do cliente.
- `status` (Enum): A situação atual do tenant na plataforma.
- `subscribedModules` (Set\<String\>): Uma lista dos identificadores dos módulos que o tenant contratou (ex: `"mod-oficina"`).
- `createdAt` (LocalDateTime): Data e hora em que o tenant foi registrado no sistema.
- `updatedAt` (LocalDateTime): Data e hora da última modificação nos dados do tenant.

### 1.3. Status do Tenant

O status de um tenant determina sua condição na plataforma e pode ser um dos seguintes:

- **ACTIVE**: O tenant está ativo e pode utilizar os módulos contratados. É o estado padrão ao criar um novo tenant.
- **INACTIVE**: O tenant foi desativado e não pode mais acessar a plataforma.
- **PENDING\_PAYMENT**: O tenant está com pagamentos pendentes, podendo ter funcionalidades restritas.
- **SUSPENDED**: O tenant foi suspenso por violação dos termos ou outra razão administrativa. O acesso é bloqueado.

## 2\. Detalhes Técnicos

Esta seção aborda a arquitetura, tecnologias e a API do serviço.

### 2.1. Arquitetura

O `tenant-service` foi construído utilizando a **Arquitetura Hexagonal (Portas e Adaptadores)**. Esta abordagem isola o núcleo de regras de negócio (o "domínio") de detalhes externos como o banco de dados ou a API web.

- **Domínio (`domain`):** Contém a lógica de negócio pura (entidades, casos de uso e portas/interfaces).
- **Adaptadores (`adapter`):** Conectam o domínio com o mundo exterior.
    - **Adaptadores de Entrada (`in`):** O `TenantController` expõe a lógica de negócio como uma API REST.
    - **Adaptadores de Saída (`out`):** O `TenantPersistenceAdapter` implementa a porta de repositório usando MongoDB.

### 2.2. Tecnologias Utilizadas

- **Java 21**: Versão da linguagem de programação (LTS).
- **Spring Boot 3.3+**: Framework principal para criação da aplicação.
- **SpringDoc OpenAPI (Swagger)**: Para geração de documentação de API interativa.
- **Spring Web**: Para a criação dos endpoints REST.
- **Spring Data MongoDB**: Para a integração e persistência com o banco de dados.
- **Maven**: Ferramenta de gerenciamento de dependências e build.
- **Lombok**: Para redução de código boilerplate.
- **Docker & Docker Compose**: Para conteinerização e orquestração do ambiente.

### 2.3. API Endpoints

A API está disponível sob o caminho base `/api/v1/tenants`. Para uma visualização completa e interativa, acesse a documentação do Swagger UI.

| Método | Endpoint                    | Descrição                                         |
| :----- | :-------------------------- | :------------------------------------------------ |
| `POST` | `/`                         | Cria um novo tenant.                              |
| `GET`  | `/`                         | Retorna uma lista de todos os tenants.            |
| `GET`  | `/{id}`                     | Busca um tenant específico pelo seu ID.           |
| `PUT`  | `/{id}/modules`             | Adiciona um novo módulo a um tenant existente.     |
| `DELETE` | `/{id}`                     | Deleta um tenant pelo seu ID.                      |

### 2.4. Documentação de API (Swagger)

O projeto gera automaticamente uma documentação de API rica e interativa usando `springdoc-openapi`. Após iniciar o serviço, a documentação estará disponível nos seguintes endereços:

- **Swagger UI (Interface Gráfica):** [http://localhost:8081/swagger-ui.html](https://www.google.com/search?q=http://localhost:8081/swagger-ui.html)
- **Definição OpenAPI (JSON):** [http://localhost:8081/v3/api-docs](https://www.google.com/search?q=http://localhost:8081/v3/api-docs)

A interface do Swagger UI é a maneira recomendada para explorar e testar os endpoints da API.

## 3\. Como Executar (com Docker)

Este serviço é projetado para ser executado como parte da plataforma via Docker Compose, que orquestra tanto a aplicação quanto suas dependências.

### 3.1. Pré-requisitos

- Docker
- Docker Compose

### 3.2. Execução

1.  Navegue até a **pasta raiz da plataforma** (o diretório que contém o arquivo `docker-compose.yml`).

2.  Execute o seguinte comando no seu terminal:

    ```bash
    docker-compose up --build
    ```

3.  O serviço estará disponível na porta `8081` da sua máquina local (`localhost`).

### 3.3. Testando

A forma recomendada para testar os endpoints é através da interface do **Swagger UI**.

1.  Após a execução, abra o seu navegador e acesse: [**http://localhost:8081/swagger-ui.html**](https://www.google.com/search?q=http://localhost:8081/swagger-ui.html)
2.  Você verá todos os endpoints listados. Expanda-os para ver detalhes, exemplos de `body` e execute-os diretamente do navegador clicando em "Try it out" -\> "Execute".