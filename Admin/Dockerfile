# Stage 1: Build the application
FROM gradle:latest AS builder

# Set the working directory
WORKDIR /app

# Copy the Gradle build files and source code
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle gradle
COPY src src

# Download dependencies and build the application
RUN gradle clean build -x test

# Stage 2: Run the application
FROM openjdk:21

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the builder stage
COPY --from=builder /app/build/libs/Admin-0.0.1-SNAPSHOT.jar .

# Set environment variables (e.g., API key)
ENV API_KEY=mF6ydJ3fNx8gGJ9k8dG94V2dJ6gJQYhH

# Expose the port that the application will run on
EXPOSE 8082

# Set the entry point to run the JAR file
ENTRYPOINT ["java", "-jar", "Admin-0.0.1-SNAPSHOT.jar"]
