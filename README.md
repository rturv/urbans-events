# Urban Events

Proyecto formativo para introducir arquitecturas event-driven con Apache Kafka.

## Componentes

- `registro-incidencias`: crea incidencias y publica `IncidenciaCreada`
- `priorizacion-incidencias`: calcula prioridad y publica `IncidenciaPriorizada`
- `notificaciones`: decide notificaciones y publica `IncidenciaNotificada`
- `metricas`: proyeccion de lectura para el frontend
- `shared-events`: modelos de eventos compartidos
- `frontend`: Angular (pendiente de scaffolding)

## Requisitos locales

- JDK 21 en `C:\programas\jdk-21.0.2`
- Maven en `C:\programas\apache-maven-3.9.9`
- Docker + Docker Compose

## Arranque rapido

1) Levantar infraestructura

```bash
docker compose up -d
```

2) Compilar todo

```bash
"C:\programas\apache-maven-3.9.9\bin\mvn" -q -DskipTests package
```

Si Maven esta usando Java 8 en tu entorno, usa el perfil local:

```bash
"C:\programas\apache-maven-3.9.9\bin\mvn" -q -P local-jdk21 -DskipTests package
```

3) Ejecutar servicios (en terminales separadas)

```bash
"C:\programas\apache-maven-3.9.9\bin\mvn" -pl registro-incidencias spring-boot:run
"C:\programas\apache-maven-3.9.9\bin\mvn" -pl priorizacion-incidencias spring-boot:run
"C:\programas\apache-maven-3.9.9\bin\mvn" -pl notificaciones spring-boot:run
"C:\programas\apache-maven-3.9.9\bin\mvn" -pl metricas spring-boot:run
```

## Endpoints

- `POST http://localhost:8081/incidencias`
  - body ejemplo:
    ```json
    {
      "tipo": "seguridad",
      "descripcion": "incendio en plaza",
      "origen": "ciudadano",
      "ubicacion": "Centro"
    }
    ```
- `GET http://localhost:8084/metricas`

## Configuracion rapida

- Kafka: `localhost:9092`
- Postgres por servicio:
  - registro: `localhost:5433`
  - priorizacion: `localhost:5434`
  - notificaciones: `localhost:5435`
  - metricas: `localhost:5436`
