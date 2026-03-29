FROM eclipse-temurin:21-jdk-jammy AS builder
LABEL authors="venya"

WORKDIR /workspace

COPY contract ./contract
COPY tg_bot ./tg_bot

WORKDIR /workspace/tg_bot

RUN chmod +x gradlew && ./gradlew --no-daemon bootJar

FROM eclipse-temurin:21-jre-jammy

WORKDIR /foreign-student/tg-bot

COPY --from=builder /workspace/tg_bot/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
