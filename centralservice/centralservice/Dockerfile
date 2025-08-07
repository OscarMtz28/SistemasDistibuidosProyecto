# Imagen oficial con Java 21 y Ubuntu LTS
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# Copia el JAR (ajusta el nombre según tu pom.xml)
COPY target/centralservice-*.jar app.jar

# Optimizaciones para contenedores y Spring Boot 3.1.5
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75 -Dfile.encoding=UTF-8"
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]