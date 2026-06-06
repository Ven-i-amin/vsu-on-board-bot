FROM gradle:9.4.1-jdk21 AS builder
LABEL authors="venya"

WORKDIR /workspace

COPY api-gateway ./api-gateway

WORKDIR /workspace/api-gateway

RUN gradle --no-daemon bootJar

FROM ibm-semeru-runtimes:open-21-jre

WORKDIR /foreign-student/api-gateway

COPY --from=builder /workspace/api-gateway/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
