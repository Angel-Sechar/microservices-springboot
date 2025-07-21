FROM openjdk:21-jdk-slim

# Metadata
LABEL maintainer="Campus E-commerce - A team"
LABEL version="0.0.1-SNAPSHOT"

# Create app directory
WORKDIR /app

# Copy the JAR file into the container
COPY target/api-gateway-0.0.1-SNAPSHOT.jar app.jar

# Create non-root user
RUN groupadd -r appuser && useradd -r -g appuser appuser
RUN chown -R appuser:appuser /app
USER appuser

# Run the gateway
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]