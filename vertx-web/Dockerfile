# Use OpenJDK 17 base image
FROM eclipse-temurin:17-jdk-jammy

# Set working directory in container
WORKDIR /app

# Copy the fat JAR built by Gradle
COPY build/libs/vertx-web-*-all.jar app.jar

# Expose the port used in your Main.java (8888)
EXPOSE 8888

# Default command
CMD ["java", "-jar", "app.jar"]
