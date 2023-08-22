FROM openjdk:11-jre-slim

ARG JAR_FILE=build/libs/be-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} upbrella-server.jar

ENTRYPOINT ["java", "-jar", "upbrella-server.jar"]
