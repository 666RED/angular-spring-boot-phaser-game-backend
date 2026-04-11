FROM eclipse-temurin:21-jdk
ARG JAR_FILE=build/libs/*.jar
COPY ./build/libs/ExpenseTrackerApp-0.0.1.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
