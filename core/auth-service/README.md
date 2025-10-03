# Core: Auth Service

O `auth-service` é o **provedor de identidade** central da Plataforma Multimodular SaaS. Sua única responsabilidade é gerenciar usuários e controlar o acesso ao ecossistema de microservices. Ele implementa a lógica para registro, autenticação via email e senha, e a geração de **Tokens de Acesso (JWT)**.

## 1\. O Papel do Auth Service na Arquitetura

Em uma arquitetura de microservices, é crucial que a lógica de autenticação não seja duplicada em cada serviço. O `auth-service` resolve isso ao centralizar as seguintes responsabilidades:

* **Gerenciamento de Identidade:** É o único serviço que tem acesso à base de dados de usuários.
* **Segurança de Credenciais:** Garante que as senhas sejam armazenadas de forma segura, usando algoritmos de hash robustos como o `BCrypt`.
* **Emissão de Tokens:** Após validar as credenciais de um usuário, ele emite um "crachá" digital (um **JSON Web Token**), que serve como prova de identidade para acessar outros serviços.
* **Desacoplamento:** Desacopla os serviços de negócio (como o `tenant-service`) da complexidade da autenticação. Eles não precisam saber como um usuário fez login, apenas que o token apresentado é válido.

## 2\. Como Funciona o Fluxo de Autenticação

O fluxo de login é orquestrado pelo Spring Security e pelos nossos componentes customizados:

1.  Um usuário envia suas credenciais (`email`, `password`) para o endpoint `POST /auth/login`.
2.  O `AuthController` recebe a requisição e chama o `AuthenticationManager` do Spring Security.
3.  O `AuthenticationManager` utiliza nosso `AuthenticationProvider` customizado.
4.  O `AuthenticationProvider` delega a busca do usuário ao nosso `UserDetailsService` (implementado na classe `UserService`).
5.  O `UserService` usa a `UserRepositoryPort` para encontrar o usuário no MongoDB pelo email.
6.  Se o usuário é encontrado, o `AuthenticationProvider` usa o `PasswordEncoder` para comparar a senha enviada com o hash armazenado no banco.
7.  Se a senha for válida, a autenticação é um sucesso. O `AuthController` então chama o `JwtService`.
8.  O `JwtService` cria um token JWT, assinado com a chave secreta (`jwt.secret`), contendo as informações do usuário (como o email).
9.  O token JWT é retornado ao cliente.

## 3\. Arquitetura

O serviço segue o padrão de **Arquitetura Hexagonal (Portas e Adaptadores)** para garantir o desacoplamento entre a lógica de negócio e as tecnologias externas.

* **`domain`**: Contém os modelos de negócio (`User`), as portas de entrada (`UseCase` interfaces) e as portas de saída (`RepositoryPort`).
* **`service`**: Implementa as interfaces de caso de uso (portas de entrada).
* **`adapter`**: Contém os adaptadores de entrada (`web/AuthController`) e de saída (`persistence/UserPersistenceAdapter`).

## 4\. Detalhes Técnicos

### 4.1. Tecnologias Utilizadas

* **Java 21**
* **Spring Boot 3.2.5** e **Spring Cloud 2023.0.1**
* **Spring Security 6**: Para todo o framework de autenticação e autorização.
* **Spring Data MongoDB**: Para persistência dos usuários.
* **JJWT**: Biblioteca para criação e manipulação de JSON Web Tokens.
* **Spring Cloud Config Client** e **Eureka Discovery Client**: Para integração com a infraestrutura da plataforma.
* **Lombok** e **Maven**.

### 4.2. Endpoints da API

| Método | Endpoint         | Descrição                                         | Acesso  |
| :----- | :--------------- | :------------------------------------------------ | :------ |
| `POST` | `/auth/register` | Cria um novo usuário na plataforma.               | Público |
| `POST` | `/auth/login`    | Autentica um usuário e retorna um token JWT.      | Público |
| `GET`  | `/auth/users`    | Lista todos os usuários cadastrados.              | Privado |

## 5\. Inicialização de Dados (`DataInitializer`)

Para facilitar o desenvolvimento e os testes, este serviço inclui um componente `DataInitializer`. Na primeira vez que a aplicação sobe, ele verifica se um usuário padrão existe e, se não, cria um:

* **Email:** `admin@admin.com`
* **Senha:** `admin123`
* **Papéis (Roles):** `ROLE_ADMIN`, `ROLE_USER`

## 6\. Configurações

As configurações críticas deste serviço são gerenciadas pelo `config-server` e estão no arquivo `auth-service.yml` do repositório `plataforma-config`.

* **`server.port`**: A porta onde o serviço roda (ex: `8082`).
* **`jwt.secret`**: A chave secreta usada para assinar e validar os tokens JWT. **Esta chave deve ser longa, segura e idêntica à configurada no API Gateway.**

## 7\. Como Executar

### 7.1. Como Parte da Plataforma (Modo Padrão)

A forma recomendada é iniciar toda a plataforma usando o Docker Compose a partir da **raiz do projeto**.

```bash
docker-compose up --build
```

### 7.2. De Forma Isolada (Para Debug)

Para depurar este serviço, você pode executá-lo diretamente pela sua IDE.

1.  Certifique-se de que as dependências (`mongodb`, `discovery-server`, `config-server`) estejam rodando. Você pode iniciá-las com o comando:
    ```bash
    docker-compose up -d mongodb discovery-server config-server
    ```
2.  Na sua IDE, execute a classe principal `AuthServiceApplication.java`.
3.  O serviço estará disponível na porta configurada (ex: `8082`).