# Estágio 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean install -DskipTests

# Estágio 2: Runtime
FROM eclipse-temurin:21-jre
WORKDIR /app
# Copia o JAR com nome fixo 'app.jar', que foi configurado no POM pai
COPY --from=build /app/infra/config-server/target/app.jar app.jar
# Expõe a porta padrão do Config Server
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]