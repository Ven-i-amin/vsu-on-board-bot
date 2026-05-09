FROM gradle:9.4.1-jdk21 AS builder
LABEL authors="venya"

WORKDIR /workspace

COPY contract ./contract
COPY front ./front
COPY core ./core

WORKDIR /workspace/core

RUN gradle --no-daemon bootJar

FROM eclipse-temurin:21-jre-jammy

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \

WORKDIR /foreign-student/core

COPY --from=builder /workspace/core/build/libs/core-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
