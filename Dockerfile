FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
COPY target/CalculatorSpringBoot-0.0.1-SNAPSHOT.jar Calculator.jar
EXPOSE 9090

ENTRYPOINT ["java", "-jar", "Calculator.jar"]