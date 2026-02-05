ARG SERVICE_NAME
FROM maven:3.9.2-eclipse-temurin-21 as builder
WORKDIR /workspace
COPY pom.xml .
COPY shared-events/pom.xml shared-events/
COPY registro-incidencias/pom.xml registro-incidencias/
COPY priorizacion-incidencias/pom.xml priorizacion-incidencias/
COPY notificaciones/pom.xml notificaciones/
COPY metricas/pom.xml metricas/
COPY shared-events shared-events
COPY registro-incidencias registro-incidencias
COPY priorizacion-incidencias priorizacion-incidencias
COPY notificaciones notificaciones
COPY metricas metricas
RUN mvn -pl ${SERVICE_NAME} -am -DskipTests package

FROM eclipse-temurin:21-jre
ARG SERVICE_NAME
WORKDIR /app
COPY --from=builder /workspace/${SERVICE_NAME}/target/*.jar ./app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]