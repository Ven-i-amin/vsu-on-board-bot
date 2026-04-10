FROM eclipse-temurin:21-jdk-jammy AS builder
LABEL authors="venya"

WORKDIR /workspace

COPY api-gateway ./api-gateway

WORKDIR /workspace/api-gateway

RUN chmod +x gradlew && ./gradlew --no-daemon bootJar

FROM eclipse-temurin:21-jre-jammy

WORKDIR /foreign-student/api-gateway

COPY --from=builder /workspace/api-gateway/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
