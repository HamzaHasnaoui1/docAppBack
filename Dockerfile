# First stage: Build the application
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Cache Maven dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Build application
COPY src /app/src
RUN mvn clean package -DskipTests -Dmaven.test.skip=true -Dmaven.main.skip=true -Dmaven.source.skip=true

# Second stage: Runtime
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Non-root user
RUN adduser --system --group appuser && \
    chown appuser:appuser /app/app.jar
USER appuser

# JVM options
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75"
EXPOSE 8087

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]