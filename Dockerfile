# --- Build stage ---
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -B

COPY src ./src
RUN ./mvnw clean package -DskipTests -B

# --- Run stage ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["sh", "-c", "java -jar app.jar \
--server.port=${PORT:-8081} \
--eureka.client.serviceUrl.defaultZone=${EUREKA_URI:-http://localhost:8761/eureka/} \
--spring.datasource.url=${DB_URL:-jdbc:mysql://localhost:8889/ong_users_db?createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false} \
--spring.datasource.username=${DB_USERNAME:-root} \
--spring.datasource.password=${DB_PASSWORD:-root} \
--security.jwt.secret-key=${JWT_SECRET:-3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b}"]
