# Stage 1
# Building the JAR
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

# Stage 2
# Building the JRE image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S app && adduser -S app -G app

COPY --from=build /build/target/*.jar app.jar

USER app
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "app.jar"]