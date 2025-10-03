# Core: API Gateway

Este serviço é o **ponto de entrada único (Single Point of Entry)** e a **primeira linha de defesa de segurança** para a Plataforma Multimodular SaaS. Implementado com **Spring Cloud Gateway** e integrado com **Spring Security**, ele atua como a fachada principal, recebendo todas as requisições externas, validando credenciais de acesso (Tokens JWT) e roteando as chamadas válidas para os microservices internos apropriados.

## 1. O Papel do API Gateway na Arquitetura

Pense no API Gateway como o **segurança na porta de um evento exclusivo**. Ele não apenas direciona os convidados (`roteamento`), mas primeiro verifica se eles têm um ingresso válido (`segurança`).

As principais responsabilidades do Gateway são:

* **Ponto de Entrada Único:** Clientes externos se comunicam apenas com o Gateway (`http://localhost:8080`), simplificando a arquitetura do frontend.
* **Roteamento Dinâmico:** Usa o `Discovery Server` (Eureka) para encontrar os serviços internos e encaminhar as requisições com base nas regras de rota definidas no `gateway.yml`.
* **Centralização da Segurança:** É o local ideal para centralizar funcionalidades que se aplicam a todas as requisições, como:
    * **Autenticação e Autorização:** Utilizando um `SecurityWebFilterChain` global, ele intercepta todas as requisições, valida os tokens JWT e bloqueia o acesso a rotas protegidas caso o token seja inválido ou inexistente.
    * **Rate Limiting:** (Futuro) Limitar o número de requisições por cliente.
    * **Logging e Monitoramento:** (Futuro) Criar um log centralizado de todo o tráfego.

## 2. Fluxo de uma Requisição Segura

Com a segurança implementada, o fluxo de uma requisição para uma rota protegida (ex: `/api/v1/tenants`) é o seguinte:

1.  Um cliente (ex: Postman) envia a requisição para `GET http://localhost:8080/api/v1/tenants`, incluindo o token no cabeçalho: `Authorization: Bearer <token_jwt>`.
2.  O **Gateway** recebe a chamada.
3.  O `SecurityWebFilterChain` (da nossa `SecurityConfig`) é o primeiro a atuar. Ele verifica a rota:
    * Se a rota for pública (ex: `/auth/login`), ele libera a passagem.
    * Se a rota for privada (nosso caso), ele aciona o `SecurityContextRepository`.
4.  O `SecurityContextRepository` extrai o token "Bearer" do cabeçalho.
5.  Ele passa o token para o `AuthenticationManager` reativo.
6.  O `AuthenticationManager` usa o `JwtService` para **validar o token**: verifica a assinatura com a chave secreta (`jwt.secret`) e a data de expiração.
7.  **Decisão de Segurança:**
    * **Token Válido:** A requisição é considerada autenticada. O fluxo continua.
    * **Token Inválido/Ausente:** O Gateway **bloqueia a requisição** e retorna `401 Unauthorized` imediatamente. O `tenant-service` nunca é acionado.
8.  **Roteamento:** Com a segurança validada, o Gateway consulta suas regras de rota (do `gateway.yml`), encontra o `tenant-service` no Eureka e encaminha a requisição.

## 3. Detalhes Técnicos

### 3.1. Tecnologias Utilizadas

* **Java 21** (LTS)
* **Spring Boot 3.2.5**
* **Spring Cloud Gateway**: Para roteamento reativo.
* **Spring Security (WebFlux)**: Para a camada de segurança reativa.
* **JJWT**: Biblioteca para parse e validação de JSON Web Tokens.
* **Spring Cloud Config Client** e **Eureka Discovery Client**: Para integração com a infraestrutura.

### 3.2. Configuração

As configurações críticas deste serviço são gerenciadas no `plataforma-config`:

* **`gateway.yml`**: Define as regras de roteamento (`routes`) e a chave secreta do JWT (`jwt.secret`), que **deve ser idêntica** à do `auth-service`. As regras de filtro (`- AuthenticationFilter`) foram removidas em favor da segurança global via Spring Security.
* **`SecurityConfig.java`**: Arquivo de configuração que define o `SecurityWebFilterChain`, estabelecendo quais rotas são públicas (`/auth/**`) e quais são protegidas.

## 4. Como Executar

### 4.1. Como Parte da Plataforma (Modo Padrão)

A forma recomendada é iniciar toda a plataforma usando o Docker Compose a partir da **raiz do projeto**.

```bash
docker-compose up --build

```

### 4.2. De Forma Isolada (Para Debug)
1. Certifique-se de que as dependências (discovery-server, config-server) estejam rodando.

```bash
docker-compose up -d discovery-server config-server
```
2. Na sua IDE, execute a classe principal GatewayApplication.java.

3. O serviço estará disponível em http://localhost:8080.