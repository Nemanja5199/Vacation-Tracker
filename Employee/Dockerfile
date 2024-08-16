
FROM gradle:latest AS builder


WORKDIR /app


COPY build.gradle.kts settings.gradle.kts ./
COPY gradle gradle
COPY src src


RUN gradle clean build -x test


FROM openjdk:21



COPY --from=builder /app/build/libs/Employee-0.0.1-SNAPSHOT.jar .





EXPOSE 8080


ENTRYPOINT ["java", "-jar", "Employee-0.0.1-SNAPSHOT.jar"]
