# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the packaged JAR file into the container at the specified working directory
COPY gradle/ /app/gradle/
COPY build.gradle /app/
COPY settings.gradle /app/

# Download and install Gradle
RUN ./gradle/wrapper/gradle-wrapper.jar

# Copy the application source code
COPY src /app/src

# Build the application using Gradle
RUN ./gradlew build

# Expose the port that your Spring Boot application will run on
EXPOSE 8080

# Specify the command to run on container start
CMD ["java", "-jar", "build/libs/github-explorer.jar"]