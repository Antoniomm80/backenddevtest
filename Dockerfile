# Multi-stage Dockerfile for Spring Boot application

# Stage 1: Build Stage
FROM maven:3.9-eclipse-temurin-25 AS builder

# Set working directory
WORKDIR /app

# Copy pom.xml first for better layer caching
COPY pom.xml .

# Download dependencies (cached layer if pom.xml doesn't change)
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Run tests
RUN mvn test

# Build the application (skip tests as they already ran)
RUN mvn package -DskipTests

# Stage 2: Runtime Stage
FROM eclipse-temurin:25-jre

# Set working directory
WORKDIR /app

# Copy the JAR file from builder stage
COPY --from=builder /app/target/backenddevtest-0.0.1-SNAPSHOT.jar app.jar

# Expose port 5000
EXPOSE 5000

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
