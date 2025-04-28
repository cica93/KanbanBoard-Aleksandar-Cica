# Use the official OpenJDK image as a base image
FROM openjdk:17-jdk-slim as builder

WORKDIR /app

COPY target/Kanban-Board-0.0.1-SNAPSHOT.jar /app/kanban-board.jar

EXPOSE 3033

# Set the environment variables
ENV SPRING_PROFILES_ACTIVE=production
ENV JAVA_OPTS="-Xmx512m"

ENTRYPOINT ["java", "-jar", "/app/kanban-board.jar"]
