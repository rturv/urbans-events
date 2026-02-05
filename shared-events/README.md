# Shared Events

Qué es
- Servicio/librería para eventos compartidos entre módulos (metadatos, encolado, consulta de eventos).

Para qué sirve
- Persistir metadatos de eventos compartidos y servirlos a otros servicios.

Cómo montarlo (desarrollo)
- Requisitos: JDK 17+, Maven, Docker si se necesita DB.
- Levantar DB y migraciones:
  ```powershell
  docker compose -f docker-compose-postresql.yml up -d postgres
  docker compose -f docker-compose-postresql.yml run --rm flyway migrate
  ```
- Ejecutar el servicio:
  ```powershell
  mvn -f shared-events spring-boot:run
  ```

API (ejemplos)
- `POST /events` - publicar un evento compartido
- `GET /events/{id}/meta` - obtener metadata del evento

Configuración
- Conexión a la base de datos y opciones de serialización JSON.

Notas
- La tabla creada por Flyway es `shared_events.evento_meta`.
