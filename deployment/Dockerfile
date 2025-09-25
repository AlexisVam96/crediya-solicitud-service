# =========================
# 1. Etapa de build (Gradle)
# =========================
FROM gradle:8.14.3-jdk17-alpine AS builder

# Configurar directorio de trabajo
WORKDIR /home/gradle/project

# Copiar archivos de configuración primero para cachear dependencias
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# Descargar dependencias (sin compilar todo aún)
RUN ./gradlew dependencies --no-daemon || return 0

# Copiar todo el código y compilar
COPY . .
RUN ./gradlew clean build -x test --no-daemon

# =========================
# 2. Etapa de runtime (JRE ligero)
# =========================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copiar el jar generado desde la etapa de build
COPY --from=builder /home/gradle/project/applications/app-service/build/libs/crediya-solicitud-service.jar app.jar

# Variables de entorno para JVM
ENV JAVA_OPTS=" -XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"

# Usuario no root (opcional pero recomendado)
RUN addgroup -S app && adduser -S app -G app
USER app

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
