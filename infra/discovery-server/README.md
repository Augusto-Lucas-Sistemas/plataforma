# Infra: Discovery Server (Eureka)

Este projeto implementa o **Service Discovery** para a Plataforma Multimodular SaaS, utilizando a solução **Spring Cloud Netflix Eureka**. Ele atua como o pilar central que permite que nossos microservices se encontrem e se comuniquem de forma dinâmica e resiliente.

## 1\. O Problema: Por Que Precisamos de um Discovery Server?

Em uma arquitetura de microservices, temos vários serviços independentes (`tenant-service`, `auth-service`, etc.) que precisam se comunicar constantemente. O desafio é: como um serviço descobre o endereço de rede (IP e porta) de outro?

A abordagem ingênua seria registrar os endereços manualmente nos arquivos de configuração. Essa abordagem falha drasticamente em ambientes modernos por duas razões:

* **Endereços Dinâmicos:** Em ambientes de contêineres como o Docker, os serviços recebem IPs dinâmicos toda vez que são iniciados. Um endereço fixo hoje pode não ser o mesmo amanhã.
* **Escalabilidade e Resiliência:** Se precisarmos subir três instâncias de um serviço para aumentar a capacidade (escalar), ou se uma instância falhar e for substituída por outra, a gestão manual desses endereços se torna impossível.

## 2\. A Solução: A "Agenda de Contatos" da Arquitetura

O Discovery Server resolve esse problema atuando como uma **agenda de contatos viva e automática** para todos os microservices.

Em vez de cada serviço ter sua própria lista de endereços desatualizada, eles seguem um ciclo simples:

1.  **Registro:** Ao iniciar, um serviço (ex: `tenant-service`) "liga" para o Discovery Server e informa: "Olá, eu sou o `TENANT-SERVICE` e meu endereço atual é `172.18.0.5:8081`". O Discovery Server armazena essa informação.
2.  **Manutenção (Heartbeat):** O serviço continua enviando um sinal de "estou vivo" (heartbeat) a cada 30 segundos. Se o Discovery Server parar de receber esses sinais, ele remove o serviço da lista de contatos ativos, garantindo que a lista seja sempre confiável.
3.  **Descoberta:** Quando outro serviço (ex: o futuro `API Gateway`) precisa falar com o `tenant-service`, ele primeiro pergunta ao Discovery Server: "Qual o endereço de um `TENANT-SERVICE` disponível?". O Discovery Server responde com o endereço atual e válido.

Com esse endereço em mãos, a comunicação é feita diretamente entre os serviços. O Discovery Server não fica no meio do caminho, ele apenas fornece a informação de contato.

## 3\. Detalhes Técnicos da Nossa Implementação

Este serviço é uma aplicação Spring Boot configurada para atuar como um servidor Eureka.

### 3.1. Tecnologias Utilizadas

* **Java 21**
* **Spring Boot 3.2.5**
* **Spring Cloud 2023.0.1**
* **Netflix Eureka Server**

### 3.2. Configurações Chave (`application.yml`)

O comportamento do Eureka Server é definido em seu arquivo de configuração:

```yaml
server:
  port: 8761

eureka:
  client:
    # Não tenta se registrar com outro servidor (ele é o próprio servidor).
    register-with-eureka: false
    # Não busca a lista de serviços de outro servidor (em um setup de um único nó).
    fetch-registry: false
```

Essas configurações são essenciais para que o servidor opere em modo "standalone" (sozinho), sem esperar por réplicas.

### 3.3. Dashboard de Monitoramento

O Eureka Server fornece uma interface web extremamente útil para visualizar o estado de toda a nossa plataforma.

* **URL de Acesso:** `http://localhost:8761`

Neste dashboard, você pode ver:

* A lista de todas as instâncias de serviços atualmente registradas (`Instances currently registered with Eureka`).
* O status de cada instância (UP, DOWN, etc.).
* Metadados sobre cada serviço, como seu endereço de IP e portas.

É a principal ferramenta para verificar se os serviços da plataforma subiram corretamente e estão se comunicando.

## 4\. Como Executar

Existem duas maneiras de executar este serviço:

### 4.1. Como Parte da Plataforma (Modo Padrão)

A forma recomendada é iniciar toda a plataforma usando o Docker Compose a partir da **raiz do projeto**.

```bash
# Na pasta raiz (plataforma/)
docker-compose up --build
```

Este comando irá construir a imagem do `discovery-server` e iniciá-lo em orquestração com os outros serviços, como o `mongodb` e o `tenant-service`.

### 4.2. De Forma Isolada (Para Debug)

Para depurar ou desenvolver especificamente este serviço, você pode executá-lo diretamente pela sua IDE (ex: IntelliJ IDEA):

1.  Abra o projeto `plataforma` na sua IDE.
2.  Navegue até o módulo `discovery-server`.
3.  Encontre e execute a classe principal `DiscoveryServerApplication.java`.

A aplicação subirá e estará disponível em `http://localhost:8761`.

## 5\. Endpoints da API

Embora a interação principal seja via Dashboard, o Eureka expõe uma API REST que é utilizada pelos "clientes" (os outros microservices) para realizar o registro e a descoberta.

O endpoint principal para inspecionar os serviços registrados via API é:

* `GET /eureka/apps`

Para usuários avançados, é possível usar ferramentas como `curl` para inspecionar o estado da plataforma diretamente pela API do Eureka. Para mais detalhes, consulte a [documentação oficial do Spring Cloud Eureka](https://www.google.com/search?q=https://docs.spring.io/spring-cloud-netflix/docs/current/reference/html/%23spring-cloud-eureka-server).