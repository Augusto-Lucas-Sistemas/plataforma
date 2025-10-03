# Estágio 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean install -DskipTests

# Estágio 2: Runtime
FROM eclipse-temurin:21-jre
WORKDIR /app
# Copia o JAR do auth-service com o nome padronizado 'app.jar'
COPY --from=build /app/core/auth-service/target/app.jar app.jar
# Expõe a porta que o serviço de autenticação usará
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]