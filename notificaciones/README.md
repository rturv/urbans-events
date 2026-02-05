# Notificaciones

Qué es
- Servicio que gestiona el envío y registro de notificaciones (email, SMS, push u otros adaptadores).

Para qué sirve
- Recibir solicitudes de notificación y persistir mensajes pendientes/enviados.
- Exponer una cola o API para disparar envíos.

Cómo montarlo (desarrollo)
- Requisitos: JDK 17+, Maven, Docker (opcional para DB y brokers si aplica).
- Levantar DB y aplicar migraciones:
  ```powershell
  docker compose -f docker-compose-postresql.yml up -d postgres
  docker compose -f docker-compose-postresql.yml run --rm flyway migrate
  ```
- Ejecutar el servicio:
  ```powershell
  mvn -f notificaciones spring-boot:run
  ```

API (ejemplos)
- `POST /mensajes` - encolar un mensaje para enviar
- `GET /mensajes` - listar mensajes
- `GET /mensajes/{id}` - ver estado de un mensaje

Configuración
- Ajustar adaptadores de envío (SMTP, proveedores externos) en `application.yml`.

Notas
- La tabla creada por Flyway es `notificaciones.mensaje`.
