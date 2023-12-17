FROM ubuntu:latest
LABEL authors="Majouo"

# Use a base image with a Java runtime
FROM eclipse-temurin:11-jdk-focal

# Set the working directory inside the container
WORKDIR /app

# Copy the application JAR file into the container
COPY out/artifacts/Blockchain_jar/Blockchain.jar /app/

EXPOSE 4000

# Command to run your application
CMD ["java", "-jar", "Blockchain.jar"]