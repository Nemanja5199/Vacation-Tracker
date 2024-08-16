# Stage 1: Build the application
FROM gradle:latest AS builder


WORKDIR /app


COPY build.gradle.kts settings.gradle.kts ./
COPY gradle gradle
COPY src src


RUN gradle clean build -x test


FROM openjdk:21


COPY --from=builder /app/build/libs/Admin-0.0.1-SNAPSHOT.jar .


ENV API_KEY=mF6ydJ3fNx8gGJ9k8dG94V2dJ6gJQYhH


EXPOSE 8082


ENTRYPOINT ["java", "-jar", "Admin-0.0.1-SNAPSHOT.jar"]
