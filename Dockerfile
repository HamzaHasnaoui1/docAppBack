FROM eclipse-temurin:17-jre as runtime
WORKDIR /app

# Configuration du timezone
ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar app.jar

# Utilisateur non-root
RUN adduser --system --group appuser
USER appuser

# Paramètres JVM optimisés
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75 -XX:+HeapDumpOnOutOfMemoryError -Djava.security.egd=file:/dev/./urandom -Dspring.main.lazy-initialization=true"

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]