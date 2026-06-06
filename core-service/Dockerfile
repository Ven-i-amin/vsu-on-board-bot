FROM node:22-alpine AS admin-panel
WORKDIR /app
COPY front/admin-panel/package*.json ./
RUN npm ci --prefer-offline
COPY front/admin-panel/ .
RUN npm run build

FROM node:22-alpine AS web-app
WORKDIR /app
COPY front/web-app/package*.json ./
RUN npm ci --prefer-offline
COPY front/web-app/ .
RUN npm run build

FROM gradle:9.4.1-jdk21 AS builder
LABEL authors="venya"

WORKDIR /workspace

COPY contract ./contract
COPY core-service ./core
COPY --from=admin-panel /app/dist ./core/src/main/resources/static/admin
COPY --from=web-app /app/dist ./core/src/main/resources/static/app

WORKDIR /workspace/core

RUN gradle --no-daemon bootJar

FROM ibm-semeru-runtimes:open-21-jre

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /foreign-student/core

COPY --from=builder /workspace/core/build/libs/core-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
