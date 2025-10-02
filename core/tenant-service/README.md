# Core: Tenant Service

Microserviço responsável pelo gerenciamento de **Tenants** (clientes) da plataforma. Ele centraliza todas as regras de negócio e operações relacionadas à criação, consulta, atualização e exclusão de tenants, bem como o controle dos módulos que cada um tem acesso.

## 1\. Regras de Negócio

Esta seção detalha os conceitos e regras que governam o domínio do `Tenant Service`.

### 1.1. O Conceito de "Tenant"

Um **Tenant** representa um cliente individual da plataforma. Cada tenant é uma entidade isolada com seus próprios usuários, dados e configurações. Exemplos de tenants seriam "Oficina do Zé" ou "Clínica Sorriso".

### 1.2. Atributos de um Tenant

- `id` (String): Identificador único (MongoDB Object ID).
- `name` (String): Nome comercial do cliente.
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

Para testes de integração ou para criar um ambiente de testes automatizados, os endpoints deste serviço estão incluídos na **coleção geral do Postman/Insomnia** da plataforma.

Esta coleção está localizada na raiz do projeto principal (`plataforma/`) no arquivo `postman_collection.json` (ou nome similar). Dentro da coleção, as requisições para o `tenant-service` estão organizadas em sua própria pasta.

Para mais informações sobre como importar e usar a coleção, consulte o `README.md` da raiz do projeto.

### 2.5. Integração com o Discovery Server (Eureka)

Este serviço é um **Discovery Client**, o que significa que ele se registra ativamente no `discovery-server` (Eureka) ao iniciar. Isso permite que outros serviços da plataforma (como o futuro API Gateway) o encontrem dinamicamente, sem precisar de endereços de rede fixos.

Essa funcionalidade é habilitada por três componentes principais:

1.  **Dependência Maven:** O `pom.xml` inclui `spring-cloud-starter-netflix-eureka-client`.
2.  **Anotação:** A classe principal `TenantServiceApplication` é anotada com `@EnableDiscoveryClient`.
3.  **Configuração:** O arquivo `application.yml` contém as propriedades que apontam para o Eureka:
    ```yaml
    eureka:
      client:
        service-url:
          defaultZone: http://discovery-server:8761/eureka/
    ```

## 3\. Como Executar

### 3.1. Como Parte da Plataforma (Modo Padrão com Docker)

A forma principal de execução é via Docker Compose, que orquestra todos os serviços da plataforma.

1.  Navegue até a **pasta raiz do projeto**.
2.  Execute o comando:
    ```bash
    docker-compose up --build
    ```
3.  O serviço estará disponível em `http://localhost:8081` e se registrará automaticamente no Discovery Server.

### 3.2. De Forma Isolada (Standalone - para Debug)

Para desenvolvimento e depuração focados neste serviço, você pode executá-lo diretamente pela sua IDE.

1.  Abra o projeto `plataforma` na sua IDE (ex: IntelliJ).
2.  **Importante:** Este serviço depende do `MongoDB` e do `Discovery Server`. Antes de prosseguir, certifique-se de que eles estejam rodando. Você pode iniciá-los separadamente com o Docker Compose:
    ```bash
    # Na raiz do projeto, inicie apenas as dependências
    docker-compose up -d mongodb discovery-server
    ```
3.  Com as dependências no ar, encontre e execute a classe principal `TenantServiceApplication.java`.
4.  O serviço estará disponível em `http://localhost:8081`.