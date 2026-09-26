# Etapa de construcción
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa de ejecución
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080

# Crear usuario sin privilegios para mayor seguridad
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

ENTRYPOINT ["java", "-jar", "app.jar"]
