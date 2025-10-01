# Estágio 1: Build da Aplicação com Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

COPY . .

# Executa o build do Maven.
RUN mvn clean install -DskipTests

# ----- LINHA DE DIAGNÓSTICO -----
# Lista o conteúdo da pasta target para podermos ver os JARs gerados.
RUN ls -l /app/core/tenant-service/target

# Estágio 2: Criação da Imagem Final de Execução
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=build /app/core/tenant-service/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]