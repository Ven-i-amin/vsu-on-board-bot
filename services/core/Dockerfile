FROM eclipse-temurin:21-jdk-jammy AS builder
LABEL authors="venya"

WORKDIR /workspace

COPY contract ./contract
COPY core ./core

WORKDIR /workspace/core

RUN chmod +x gradlew && ./gradlew --no-daemon bootJar

FROM eclipse-temurin:21-jre-jammy

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /foreign-student/core

COPY --from=builder /workspace/core/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
