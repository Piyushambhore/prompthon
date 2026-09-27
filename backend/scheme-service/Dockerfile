# Multi-stage Dockerfile for JanSaarthi Scheme Service (Kotlin / Ktor)

# Stage 1: Build application binary with Gradle & JDK 17
FROM gradle:8.5-jdk17 AS builder
WORKDIR /home/gradle/project

# Copy Gradle build configuration files
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY src ./src

# Build distribution bundle without daemon
RUN gradle installDist --no-daemon

# Stage 2: Minimal, secure Alpine runtime image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create non-privileged service user for security
RUN addgroup -S jansaarthi && adduser -S jansaarthi -G jansaarthi

# Copy distribution from builder stage
COPY --from=builder --chown=jansaarthi:jansaarthi /home/gradle/project/build/install/scheme-service .

USER jansaarthi

EXPOSE 8080

ENV PORT=8080

CMD ["sh", "-c", "./bin/scheme-service"]
