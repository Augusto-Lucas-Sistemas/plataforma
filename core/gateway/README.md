# Core: API Gateway

Este serviço é o **ponto de entrada único (Single Point of Entry)** para a Plataforma Multimodular SaaS. Implementado com **Spring Cloud Gateway**, ele atua como a fachada principal, recebendo todas as requisições externas e roteando-as de forma inteligente para os microservices internos apropriados.

## 1\. O Papel do API Gateway na Arquitetura

Pense no API Gateway como o **porteiro ou a recepcionista de um grande prédio comercial**. Em vez de um visitante (o cliente da API) precisar saber o andar e a sala de cada empresa (cada microserviço), ele se dirige a um único local — a recepção. A recepcionista então o direciona para o lugar certo.

As principais responsabilidades do Gateway são:

* **Ponto de Entrada Único:** Clientes externos (como um frontend web ou mobile) se comunicam apenas com o Gateway (ex: `http://localhost:8080`). Eles não precisam conhecer os endereços dos serviços internos.
* **Roteamento Dinâmico:** O Gateway usa o `Discovery Server` (Eureka) para saber onde os outros serviços estão. Ele encaminha as requisições com base em regras (predicados), como o caminho da URL.
* **Fachada Simplificada:** Ele pode simplificar APIs complexas, orquestrando chamadas para múltiplos serviços internos e retornando uma resposta agregada.
* **Centralização de Lógica Transversal:** É o local ideal para centralizar funcionalidades que se aplicam a todas as requisições, como:
    * **Segurança:** (Próximo passo) Validar tokens de autenticação (JWT) antes de permitir que uma requisição prossiga.
    * **Rate Limiting:** Limitar o número de requisições por cliente.
    * **Logging e Monitoramento:** Criar um log centralizado de todo o tráfego que entra na plataforma.

## 2\. Como Funciona o Roteamento

O roteamento é a principal função do Gateway. O fluxo de uma requisição é o seguinte:

1.  Um cliente (ex: Postman) faz uma chamada para `GET http://localhost:8080/api/v1/tenants`.
2.  O **Gateway** recebe esta requisição na porta `8080`.
3.  Ele consulta suas regras de roteamento, que foram carregadas do **Config Server**.
4.  Ele encontra uma regra que corresponde ao padrão do caminho: `Path=/api/v1/tenants/**`.
5.  A regra diz para encaminhar a requisição para o serviço `uri: lb://TENANT-SERVICE`.
    * O prefixo `lb://` instrui o Gateway a usar o **Load Balancer** integrado com o **Discovery Server (Eureka)** para encontrar o endereço de um serviço chamado `TENANT-SERVICE`.
6.  O Gateway pergunta ao Eureka: "Onde está o `TENANT-SERVICE`?". O Eureka responde com o endereço de rede interno do contêiner (ex: `172.18.0.5:8081`).
7.  O Gateway, então, repassa a requisição original para o `tenant-service` nesse endereço.
8.  O `tenant-service` processa a requisição, retorna a resposta ao Gateway, que por sua vez a entrega ao cliente original.

## 3\. Detalhes Técnicos

### 3.1. Tecnologias Utilizadas

* **Java 21** (LTS)
* **Spring Boot 3.2.5**
* **Spring Cloud 2023.0.1**
* **Spring Cloud Gateway**: Baseado em um stack não-bloqueante (Project Reactor, Netty) para alta performance.
* **Spring Cloud Config Client**: Para consumir suas configurações (rotas) do `config-server`.
* **Spring Cloud Netflix Eureka Client**: Para se registrar e descobrir outros serviços.

### 3.2. Configuração de Rotas

As regras de roteamento do Gateway **não estão neste projeto**. Elas são gerenciadas externamente no repositório `plataforma-config`, dentro do arquivo `gateway.yml`. Isso nos dá a flexibilidade de alterar o roteamento da plataforma sem precisar fazer um novo deploy do Gateway.

**Exemplo (`plataforma-config/gateway.yml`):**

```yaml
spring:
  cloud:
    gateway:
      routes:
        # Rota para o Tenant Service
        - id: tenant_service_route
          uri: lb://TENANT-SERVICE
          predicates:
            - Path=/api/v1/tenants/**
```

* **`id`**: Um nome único para a rota.
* **`uri`**: O destino da requisição. `lb://` indica que o nome a seguir (`TENANT-SERVICE`) deve ser resolvido via Discovery Server.
* **`predicates`**: As condições para que a rota seja ativada. `Path` é a mais comum, baseada no caminho da URL.

## 4\. Como Executar

### 4.1. Como Parte da Plataforma (Modo Padrão)

A forma recomendada é iniciar toda a plataforma usando o Docker Compose a partir da **raiz do projeto**.

```bash
# Na pasta raiz (plataforma/)
docker-compose up --build
```

Este comando irá construir a imagem do `gateway` e iniciá-lo em orquestração com os outros serviços.

### 4.2. De Forma Isolada (Para Debug)

Para depurar ou desenvolver especificamente este serviço, você pode executá-lo diretamente pela sua IDE.

1.  Abra o projeto `plataforma` na sua IDE (ex: IntelliJ).
2.  **Importante:** Este serviço depende do `Discovery Server` e do `Config Server`. Antes de prosseguir, certifique-se de que eles estejam rodando.
    ```bash
    # Na raiz do projeto, inicie apenas as dependências de infraestrutura
    docker-compose up -d discovery-server config-server
    ```
3.  Com as dependências no ar, encontre e execute a classe principal `GatewayApplication.java`.
4.  O serviço estará disponível em `http://localhost:8080`.

## 5\. Próximos Passos para este Serviço

O próximo grande passo para o Gateway será a implementação de **filtros de segurança globais** para validar tokens de autenticação (JWT) em conjunto com o futuro `auth-service`.