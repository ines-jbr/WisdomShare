# Build stage
FROM maven:3.9.4-openjdk-17-slim as build

WORKDIR /app

COPY pom.xml .

COPY src ./src

RUN mvn clean package -DskipTests

# Run stage
FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8088

CMD ["java", "-jar", "app.jar"]