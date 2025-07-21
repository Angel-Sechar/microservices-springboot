FROM openjdk:21-jdk-slim

#Metadata
LABEL maintainer="Campus E-commerce - A team"
LABEL version="0.0.1-SNAPSHOT"

# Create app directory
WORKDIR /app

# Copy jar file
COPY target/auth-service-0.0.1-SNAPSHOT.jar app.jar

# Create non-root user for security
RUN groupadd -r appuser && useradd -r -g appuser appuser
RUN chown -R appuser:appuser /app
USER appuser

# Run application
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]