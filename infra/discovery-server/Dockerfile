# Estágio 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
# Padronizado para 'install', que é mais seguro em builds multi-módulo
RUN mvn clean install -DskipTests

# Estágio 2: Runtime
FROM eclipse-temurin:21-jre
WORKDIR /app
# Copia o JAR com nome fixo 'app.jar', conforme configurado no POM pai
COPY --from=build /app/infra/discovery-server/target/app.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]