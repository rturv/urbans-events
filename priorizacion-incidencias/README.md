# Priorización Incidencias

Qué es
- Servicio que determina la prioridad de las incidencias y gestiona las reglas de priorización.

Para qué sirve
- Calcular y exponer niveles de prioridad para incidencias.
- Permitir CRUD básico sobre prioridades.

Cómo montarlo (desarrollo)
- Requisitos: JDK 17+, Maven, Docker (opcional para DB).
- Levantar DB y migraciones (desde la raíz):
  ```powershell
  docker compose -f docker-compose-postresql.yml up -d postgres
  docker compose -f docker-compose-postresql.yml run --rm flyway migrate
  ```
- Ejecutar el servicio:
  ```powershell
  mvn -f priorizacion-incidencias spring-boot:run
  ```

API (ejemplos)
- `GET /prioridades` - listar prioridades
- `POST /prioridades` - crear prioridad
- `GET /prioridades/{id}` - obtener prioridad

Configuración
- Variables de conexión JDBC en `application.yml` o env vars.

Notas
- La tabla creada por Flyway es `priorizacion_incidencias.prioridad`.
