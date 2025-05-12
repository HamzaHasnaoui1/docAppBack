# Stage 1: Build the application
FROM maven:3.6.3-jdk-8 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml file and download dependencies
COPY pom.xml /app/
RUN mvn dependency:go-offline -B

# Copy the source code into the container
COPY src /app/src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM openjdk:8-jre-slim

# Set the working directory in the container
WORKDIR /app

# Copy the jar file from the build stage
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar /app/demo-0.0.1-SNAPSHOT.jar

# Expose the application port
EXPOSE 8088

# Run the application
ENTRYPOINT ["java", "-jar", "/app/demo-0.0.1-SNAPSHOT.jar"]
