FROM eclipse-temurin:17-jre

RUN mkdir -p /opt/app
COPY build/libs/orbit.jar /opt/app/app.jar
CMD ["java", "-jar", "/opt/app/app.jar"]
