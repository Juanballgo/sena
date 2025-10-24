FROM eclipse-temurin:17-jdk

EXPOSE 8081

LABEL MAINTAINER="Juan Pablo"

COPY target/sena-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT [ "java", "-jar", "/app.jar" ]

