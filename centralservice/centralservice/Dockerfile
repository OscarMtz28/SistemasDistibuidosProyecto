FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY target/centralservice-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]