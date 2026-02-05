# Notificaciones Quarkus

## ¿Qué es?
Servicio implementado con Quarkus que gestiona el envío y registro de notificaciones (email, SMS, push u otros adaptadores). Es una versión alternativa del servicio `notificaciones` usando la pila tecnológica de Quarkus.

## ¿Para qué sirve?
- Consume eventos de incidencias priorizadas desde Kafka
- Filtra y procesa solo incidencias de prioridad "alta"
- Registra notificaciones enviadas en la base de datos PostgreSQL
- Publica eventos de incidencias notificadas a Kafka

## Stack Tecnológico
- **Quarkus 3.31.2**
- **REST**: quarkus-rest
- **ORM**: Hibernate ORM with Panache (quarkus-hibernate-orm-panache)
- **Database**: JDBC Driver – PostgreSQL (quarkus-jdbc-postgresql)
- **Email**: Mailer (quarkus-mailer)
- **Messaging**: Kafka Connector (quarkus-messaging-kafka)

## Requisitos
- JDK 21+
- Maven 3.x
- PostgreSQL (contenedor Docker)
- Kafka (contenedor Docker)

## Cómo montarlo (desarrollo)

### 1. Levantar infraestructura
```powershell
# Levantar PostgreSQL
docker compose -f docker-compose-postresql.yml up -d postgres

# Aplicar migraciones
docker compose -f docker-compose-postresql.yml run --rm flyway migrate

# Levantar Kafka
docker compose up -d kafka
```

### 2. Ejecutar el servicio

**Modo desarrollo (con live reload):**
```powershell
cd notificaciones-quarkus
mvn quarkus:dev
```

**Modo producción:**
```powershell
mvn clean package
java -jar target/quarkus-app/quarkus-run.jar
```

## Configuración

El servicio se configura a través de [application.properties](src/main/resources/application.properties):

- **Puerto HTTP**: 8084
- **Base de datos**: Schema `notificaciones` en PostgreSQL
- **Kafka topics**: 
  - Consume: `incidencias.priorizadas`
  - Produce: `incidencias.notificadas`
- **Destinatarios**: Configurable en `notificaciones.destinatarios`

## Flujo de Mensajes

1. **Consume** eventos `IncidenciaPriorizadaEvent` desde el topic `incidencias.priorizadas`
2. **Filtra** solo incidencias con prioridad = "alta"
3. **Persiste** la notificación en la tabla `notificaciones.notificaciones`
4. **Produce** evento `IncidenciaNotificadaEvent` al topic `incidencias.notificadas`

## Estructura del Proyecto

```
notificaciones-quarkus/
├── src/main/java/com/urbanevents/notificaciones/
│   ├── consumer/
│   │   └── IncidenciaPriorizadaConsumer.java
│   └── domain/
│       └── Notificacion.java
└── src/main/resources/
    └── application.properties
```

## Notas
- Usa Panache Entity para simplificar el acceso a datos
- Procesa mensajes de forma reactiva con Mutiny (Uni)
- Las transacciones se gestionan automáticamente con `@Transactional`
- Schema de base de datos: `notificaciones.notificaciones`

---

## Guías de Quarkus

Si deseas aprender más sobre Quarkus, visita: <https://quarkus.io/>

### Guías relacionadas
- REST ([guide](https://quarkus.io/guides/rest))
- Messaging - Kafka Connector ([guide](https://quarkus.io/guides/kafka-getting-started))
- Hibernate ORM with Panache ([guide](https://quarkus.io/guides/hibernate-orm-panache))
- Mailer ([guide](https://quarkus.io/guides/mailer))
- JDBC Driver - PostgreSQL ([guide](https://quarkus.io/guides/datasource))
