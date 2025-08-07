FROM eclipse-temurin:21-jdk-jammy as builder
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=builder /app/target/p2pnodo-*.jar app.jar
RUN mkdir -p /app/fragments
EXPOSE ${SERVER_PORT}
ENTRYPOINT ["java", "-jar", "app.jar"]