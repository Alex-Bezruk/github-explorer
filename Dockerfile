# Stage 1: Build the application
FROM openjdk:17-alpine AS build
WORKDIR /app
COPY gradle/ /app/gradle/
COPY build.gradle settings.gradle /app/
COPY src /app/src
COPY gradlew /app/
RUN chmod +x /app/gradlew && /app/gradlew --version
RUN /app/gradlew build

# Stage 2: Create a smaller image for runtime
FROM openjdk:17-alpine
WORKDIR /app
COPY --from=build /app/build/libs/github-explorer-1.0.0.jar /app/
EXPOSE 8080
CMD ["java", "-jar", "github-explorer-1.0.0.jar"]