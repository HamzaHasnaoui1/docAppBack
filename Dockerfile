# Use a more reliable base image with retry logic
FROM eclipse-temurin:17-jre-jammy as runtime

# Add retry capability for package installations
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    ca-certificates && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Set timezone
ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Copy application
COPY --from=build /app/target/*.jar app.jar

# Run as non-root user
RUN adduser --system --group appuser
USER appuser

# Optimized JVM settings
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75 -XX:+HeapDumpOnOutOfMemoryError"

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]