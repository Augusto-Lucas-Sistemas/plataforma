# Estágio 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean install -DskipTests

# Estágio 2: Runtime
FROM eclipse-temurin:21-jre

# Instala o curl para uso no healthcheck do Docker Compose
RUN apt-get update && apt-get install -y curl

WORKDIR /app
COPY --from=build /app/infra/discovery-server/target/app.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
