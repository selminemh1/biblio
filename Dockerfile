# =============================================================
# Dockerfile — Library Management System
# DevSecOps : image minimale, user non-root, multi-stage
# =============================================================

# ─────────────────────────────────────────
# Stage 1 : BUILD
# ─────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

# 1. Copier pom.xml seul → cache Maven (layers Docker optimisés)
#    Si pom.xml ne change pas, Maven ne re-télécharge pas les dépendances
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 2. Copier le code source
COPY src ./src

# 3. Compiler et packager sans les tests
#    (les tests tournent dans le pipeline CI/CD : JUnit + OWASP)
RUN mvn package -DskipTests -B

# ─────────────────────────────────────────
# Stage 2 : RUNTIME (image finale légère)
# ─────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

# Métadonnées de l'image
LABEL maintainer="PFA Library Project"
LABEL version="1.0.0"
LABEL description="Library Management System - Spring Boot"

WORKDIR /app

# DevSecOps : créer un utilisateur non-root (jamais tourner en root)
RUN addgroup -S librarygroup && adduser -S libraryuser -G librarygroup

# Copier uniquement le JAR depuis le stage builder
COPY --from=builder /app/target/library-management-1.0.0.jar app.jar

# Changer le propriétaire du fichier
RUN chown libraryuser:librarygroup app.jar

# Basculer vers l'utilisateur non-root
USER libraryuser

# Port de l'application (doit correspondre à server.port)
EXPOSE 8081

# Démarrage avec profil "docker" → lit application-docker.properties
ENTRYPOINT ["java", \
  "-jar", \
  "-Dspring.profiles.active=docker", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "app.jar"]