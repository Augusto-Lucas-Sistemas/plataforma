# Infra: Config Server

Este serviço implementa o padrão de **Configuração Centralizada** para a Plataforma Multimodular SaaS, utilizando o **Spring Cloud Config**. Sua função é ser a fonte única de verdade para todas as propriedades de configuração dos microservices da plataforma.

## 1\. O Problema: Por Que Centralizar as Configurações?

Em uma arquitetura com muitos microservices, cada um teria seu próprio arquivo `application.yml`. Isso gera vários problemas:

* **Manutenção Difícil:** Alterar uma propriedade comum (como a URL de um banco de dados) exige a modificação de múltiplos arquivos em diferentes projetos.
* **Gestão de Ambientes:** Gerenciar configurações para diferentes ambientes (desenvolvimento, teste, produção) se torna complexo e propenso a erros.
* **Risco de Segurança:** Informações sensíveis, como senhas e chaves de API, ficam espalhadas por vários repositórios de código-fonte.

## 2\. A Solução: Um Repositório Git como Fonte da Verdade

O Spring Cloud Config Server resolve esses problemas ao externalizar as configurações. Em nossa arquitetura, ele funciona da seguinte maneira:

1.  Todas as propriedades de configuração (`.yml`) são armazenadas em um **repositório Git dedicado** (`plataforma-config`).
2.  O `Config Server` é configurado para "observar" este repositório.
3.  Quando qualquer outro microserviço (um "cliente") inicia, ele se conecta ao `Config Server` e solicita suas próprias configurações.
4.  O `Config Server` busca o arquivo apropriado no Git e entrega as propriedades ao serviço solicitante.

Isso nos garante **centralização**, **versionamento de alterações** (auditoria via `git log`) e **segurança**, pois o código da aplicação não contém mais dados sensíveis.

## 3\. Como Funciona em Nossa Arquitetura

O `config-server` é um cidadão de primeira classe em nosso ecossistema, interagindo diretamente com o `discovery-server`. O fluxo de inicialização de um serviço como o `tenant-service` é o seguinte:

1.  O `discovery-server` e o `config-server` são iniciados pelo Docker Compose.
2.  O `config-server` se registra no `discovery-server` (Eureka) com o nome `CONFIG-SERVER`.
3.  O `tenant-service` inicia. Graças à sua configuração (`spring.config.import: "configserver:"`), seu primeiro passo é procurar o `config-server`.
4.  Ele pergunta ao `discovery-server`: "Onde está o `CONFIG-SERVER`?"
5.  O Eureka responde com o endereço de rede do `config-server`.
6.  O `tenant-service` se conecta ao `config-server` e pede suas configurações.
7.  O `config-server` busca o arquivo `tenant-service.yml` no repositório Git `plataforma-config` e retorna seu conteúdo.
8.  O `tenant-service` aplica as propriedades recebidas (como `server.port: 8081`) e continua sua inicialização normalmente.

## 4\. Detalhes Técnicos

### 4.1. Tecnologias Utilizadas

* **Java 21** (LTS)
* **Spring Boot 3.2.5**
* **Spring Cloud 2023.0.1**
* **Spring Cloud Config Server**
* **Spring Cloud Netflix Eureka Client** (para se registrar no Discovery)
* **Maven** e **Docker**

### 4.2. Estrutura do Repositório Git de Configurações

O repositório Git (`plataforma-config`) segue uma convenção de nomenclatura simples para organizar os arquivos:

* **`application.yml`**: Propriedades globais, compartilhadas por **todos** os serviços.
* **`{application-name}.yml`**: Propriedades específicas para um serviço. Ex: `tenant-service.yml`.
* **`{application-name}-{profile}.yml`**: Propriedades específicas para um serviço em um determinado ambiente (profile). Ex: `tenant-service-production.yml`.

## 5\. Como Executar

### 5.1. Como Parte da Plataforma (Modo Padrão)

A forma recomendada é iniciar toda a plataforma usando o Docker Compose a partir da **raiz do projeto**.

```bash
# Na pasta raiz (plataforma/)
docker-compose up --build
```

Este comando irá construir a imagem do `config-server`, iniciá-lo em orquestração com os outros serviços e garantir a ordem de inicialização correta.

### 5.2. De Forma Isolada (Para Debug)

Para depurar este serviço, você pode executá-lo diretamente pela sua IDE (ex: IntelliJ):

1.  Abra o projeto `plataforma` na sua IDE.
2.  **Importante:** Este serviço depende do `Discovery Server` para se registrar. Certifique-se de que ele esteja rodando (ex: `docker-compose up -d discovery-server`).
3.  Navegue até o módulo `config-server` e execute a classe principal `ConfigServerApplication.java`.
4.  O serviço estará disponível em `http://localhost:8888`.

## 6\. Endpoints da API

A principal forma de interagir com o `Config Server` é para inspecionar as configurações que ele está servindo. O formato da URL é:

`/{application}/{profile}`

* **`{application}`**: O nome do serviço (ex: `tenant-service`).
* **`{profile}`**: O ambiente/perfil desejado (ex: `default`, `development`, `production`).

**Exemplo Prático:**
Para verificar as propriedades que o `tenant-service` está recebendo, acesse no seu navegador:

* [http://localhost:8888/tenant-service/default](https://www.google.com/search?q=http://localhost:8888/tenant-service/default)

A resposta será um JSON mostrando os arquivos de configuração aplicados e todas as propriedades lidas do repositório Git.