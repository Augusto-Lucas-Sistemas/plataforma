# Plataforma Multimodular SaaS

[](https://github.com/actions)
[](https://www.google.com/search?q=./LICENSE)
[](https://www.google.com/search?q=http://localhost:8081/swagger-ui.html)

Uma plataforma robusta e escalável, construída em Java com Spring Boot, projetada para servir como um software como serviço (SaaS) multitenant. A arquitetura é baseada em microservices e visa oferecer diferentes módulos de negócio para clientes distintos de forma isolada e segura.

## 1\. Visão Geral da Plataforma

O objetivo deste projeto é criar uma única plataforma base que possa atender a diversos nichos de mercado através de módulos específicos. Por exemplo, a mesma plataforma pode servir um módulo de "Oficina Mecânica" para um cliente e um módulo de "Consultório Médico" para outro.

**Principais Conceitos:**

- **Multitenancy:** Uma única instância da aplicação serve múltiplos clientes (tenants), com total isolamento de dados e funcionalidades.
- **Modularidade:** As funcionalidades de negócio são encapsuladas em módulos independentes que podem ser "habilitados" para cada cliente de acordo com o plano contratado.

## 2\. Arquitetura da Plataforma

A plataforma adota uma **Arquitetura de Microservices** para garantir escalabilidade, resiliência e manutenibilidade. Cada serviço é um componente independente, com suas próprias responsabilidades e, em muitos casos, seu próprio banco de dados.

### 2.1. Diagrama de Contêineres (Modelo C4)

O diagrama abaixo ilustra a visão de alto nível dos principais serviços e como eles interagem.

```mermaid
graph TD
    subgraph "Cliente"
        U[Usuário Final (Navegador)]
    end

    subgraph "Plataforma SaaS (Docker)"
        G[API Gateway]

        subgraph "Serviços de Core"
            AS[Auth Service]
            TS[Tenant Service]
        end

        subgraph "Módulos de Negócio"
            M1[Módulo Oficina]
            M2[Módulo Consultório]
            M3[...]
        end

        subgraph "Serviços de Infraestrutura"
            DS[Discovery Server]
            CS[Config Server]
        end

        subgraph "Bancos de Dados"
            DB_TS[(MongoDB - Tenants)]
            DB_M1[(DB do Módulo 1)]
            DB_M2[(DB do Módulo 2)]
        end
    end

    U -- HTTPS --> G

    G --> AS
    G --> TS
    G --> M1
    G --> M2
    G --> M3

    AS -- "Valida permissões" --> TS
    M1 -- "Consulta dados do Tenant" --> TS

    TS --> DB_TS
    M1 --> DB_M1
    M2 --> DB_M2

    AS -- "Registra-se em" --> DS
    TS -- "Registra-se em" --> DS
    M1 -- "Registra-se em" --> DS
    G -- "Descobre serviços em" --> DS

    AS -- "Busca configurações de" --> CS
    TS -- "Busca configurações de" --> CS
    M1 -- "Busca configurações de" --> CS
    G -- "Busca configurações de" --> CS

```

## 3\. Estrutura de Módulos e Serviços

O repositório está organizado em três pastas principais que agrupam os serviços por responsabilidade:

### 📁 `core/` - Serviços Essenciais

Serviços que formam o núcleo da plataforma.

| Serviço | Responsabilidade | Status |
| :--- | :--- |:--- |
| **`tenant-service`** | Gerencia os clientes (tenants) e os módulos que eles assinam. | ✅ **Implementado** |
| **`auth-service`** | Cuida da autenticação (login/senha) e autorização (tokens JWT). | 📝 Planejado |
| **`gateway`** | Ponto de entrada único (Single Point of Entry) para todas as requisições externas. Roteia, aplica filtros de segurança e agrega respostas. | 📝 Planejado |

### 📁 `infra/` - Serviços de Infraestrutura

Serviços que dão suporte à arquitetura de microservices.

| Serviço | Responsabilidade | Status |
| :--- | :--- |:--- |
| **`discovery-server`** | Permite que os serviços se encontrem dinamicamente na rede (Service Discovery). Será usado [Eureka](https://github.com/Netflix/eureka). | 📝 Planejado |
| **`config-server`** | Centraliza as configurações de todos os microservices em um único local. | 📝 Planejado |

### 📁 `modules/` - Módulos de Negócio

Módulos específicos de cada nicho de mercado, contendo a lógica de negócio do cliente final.

| Serviço | Responsabilidade | Status |
| :--- | :--- |:--- |
| **`mod-oficina`** | Exemplo de módulo para gerenciamento de uma oficina. | 📝 Planejado |
| **`mod-consultorio`** | Exemplo de módulo para agendamentos em um consultório. | 📝 Planejado |

## 4\. Arquitetura do Serviço Individual

Para garantir consistência, manutenibilidade e baixo acoplamento, todos os serviços de negócio e de core devem seguir o padrão de **Arquitetura Hexagonal (Portas e Adaptadores)**.

Isso significa que o núcleo de cada serviço (contendo a lógica de negócio) é completamente isolado de tecnologias externas. A comunicação com o mundo (APIs REST, bancos de dados, filas de mensagens) é feita através de "Portas" (interfaces) e "Adaptadores" (implementações).

Para um mergulho profundo nesta arquitetura, consulte o `README.md` de cada serviço individual (ex: [`core/tenant-service/README.md`](https://www.google.com/search?q=./core/tenant-service/README.md)).

## 5\. Ambiente de Desenvolvimento com Docker

Toda a plataforma é orquestrada com Docker e Docker Compose para garantir um ambiente de desenvolvimento consistente e fácil de configurar.

### 5.1. Pré-requisitos

- Git
- JDK 21 (LTS) - Para a IDE reconhecer e compilar o código.
- Maven 3.8+ - Para gerenciamento de dependências.
- Docker e Docker Compose - Para executar a plataforma integrada.

### 5.2. Como Executar a Plataforma

1.  Clone este repositório.

2.  Navegue até a pasta raiz `plataforma/`.

3.  Execute o seguinte comando:

    ```bash
    docker-compose up --build
    ```

4.  O comando irá construir as imagens de cada serviço e iniciar todos os contêineres definidos no arquivo `docker-compose.yml`.

### 5.3. Acesso aos Serviços

Após a execução, os principais pontos de acesso estarão disponíveis em `localhost`:

| Serviço | URL de Acesso | Coleção do Postman |
| :--- | :--- |:--- |
| **API Gateway** | `http://localhost:8080` | (a ser criada) |
| **Tenant Service** | `http://localhost:8081` | [`core/tenant-service/tenant-service-collection.json`](https://www.google.com/search?q=./core/tenant-service/tenant-service-collection.json) |
| **Discovery Server** | `http://localhost:8761` | N/A |

## 6\. Documentação

### 6.1. Documentação de API (Swagger)

Cada microserviço que expõe uma API REST gera automaticamente sua própria documentação interativa usando **`springdoc-openapi`**.

- **Swagger UI (Tenant Service):** `http://localhost:8081/swagger-ui.html`
- **Definição OpenAPI (Tenant Service):** `http://localhost:8081/v3/api-docs`

A interface do Swagger UI é a maneira recomendada para explorar e testar os endpoints de cada API individualmente.

### 6.2. Coleções de Teste (Postman)

Para facilitar os testes e a exploração das APIs, cada microserviço contém sua própria coleção do Postman. O arquivo de importação pode ser encontrado na raiz de cada módulo e segue o padrão de nomenclatura: `[nome-do-servico]-collection.json`.

Estas coleções já vêm com variáveis de ambiente (como `baseUrl`) e exemplos de requisições.

## 7\. Próximos Passos e Contribuição

Este é um projeto em evolução. Os próximos passos incluem a implementação dos serviços planejados, começando pela infraestrutura (`discovery-server`, `config-server`) e o serviço de autenticação (`auth-service`).

Ao contribuir, por favor, siga os padrões de arquitetura e documentação já estabelecidos.