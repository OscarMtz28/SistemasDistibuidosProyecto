FROM eclipse-temurin:21-jdk-jammy

# Configura el puerto via ARG (sobrescribible en docker-compose)
ARG SERVER_PORT=8081
ENV SERVER_PORT=$SERVER_PORT

WORKDIR /app

# Copia el JAR y crea directorio para fragmentos
COPY target/p2pnodo-*.jar app.jar
RUN mkdir -p /app/fragments

# Optimizaciones JVM
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75"

# Expone el puerto dinámico
EXPOSE $SERVER_PORT

ENTRYPOINT ["java", "-jar", "app.jar"]