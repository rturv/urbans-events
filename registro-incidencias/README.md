# Registro Incidencias

Qué es
- Servicio responsable de registrar eventos/incidencias generadas en la ciudad.

Para qué sirve
- Recibir, validar y persistir incidencias.
- Proveer APIs para consultar y actualizar el estado de una incidencia.

Cómo montarlo (desarrollo)
- Requisitos: JDK 17+, Maven, Docker (opcional para DB).
- Levantar PostgreSQL y migraciones (desde la raíz del proyecto):
  ```powershell
  docker compose -f docker-compose-postresql.yml up -d postgres
  docker compose -f docker-compose-postresql.yml run --rm flyway migrate
  ```
- Ejecutar el servicio:
  ```powershell
  mvn -f registro-incidencias spring-boot:run
  ```

API (ejemplos - ajustar según implementación)
- `POST /incidencias` - crear incidencia (payload JSON)
- `GET /incidencias/{id}` - recuperar incidencia por id
- `GET /incidencias` - listar/incidencias con filtros (`estado`, `tipo`)
- `PUT /incidencias/{id}/estado` - cambiar el estado de una incidencia

Configuración
- Usa `application.yml` o variables de entorno para `spring.datasource.*`.

Notas
- La tabla creada por Flyway es `registro_incidencias.incidencias`.
