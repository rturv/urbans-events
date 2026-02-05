# Métricas

Qué es
- Servicio que expone métricas y recolecta datos operativos del sistema.

Para qué sirve
- Proveer métricas para monitorización (endpoints, latencias, contadores).

Cómo montarlo (desarrollo)
- Requisitos: JDK 17+, Maven.
- Ejecutar el servicio:
  ```powershell
  mvn -f metricas spring-boot:run
  ```

API / Endpoints (ejemplos)
- `GET /actuator/metrics` - métricas Actuator (si habilitado)
- `GET /metrics/custom` - endpoint personalizado de métricas

Configuración
- Habilitar `spring-boot-actuator` y exporters (Prometheus) en `application.yml`.

Notas
- Este módulo suele integrarse con sistemas de monitorización externos.
