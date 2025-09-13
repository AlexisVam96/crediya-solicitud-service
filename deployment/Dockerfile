# Stage 1: Build the JAR with Gradle
FROM gradle:8.5-jdk17-alpine AS build
WORKDIR /app
COPY . .
RUN gradle :applications:app-service:build --no-daemon

# Stage 2: Run the JAR with a lightweight JDK
FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY applications/app-service/build/libs/crediya-solicitud-service.jar crediya-solicitud-service.jar
#ENV JAVA_OPTS=" -Xshareclasses:name=cacheapp,cacheDir=/cache,nonfatal -XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"
ENV JAVA_OPTS=" -XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"
# Replace with a non-root user to avoid running the container with excessive privileges
# USER appuser
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS  -jar crediya-solicitud-service.jar" ]