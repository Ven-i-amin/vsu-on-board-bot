FROM gradle:9.4.1-jdk21 AS builder
LABEL authors="venya"

WORKDIR /workspace

COPY contract ./contract
COPY tg-bot-service ./tg-bot

WORKDIR /workspace/tg-bot

RUN gradle --no-daemon bootJar

FROM eclipse-temurin:21-jre-jammy
WORKDIR /foreign-student/tg-bot

COPY --from=builder /workspace/tg-bot/build/libs/foreign_bot-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]