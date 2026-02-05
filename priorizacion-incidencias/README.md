# Priorización Incidencias

## Qué es
Servicio que determina automáticamente la prioridad de las incidencias basándose en palabras clave y publica eventos de incidencias priorizadas.

## Arquitectura
Este servicio utiliza **Spring Cloud Stream** con un modelo de programación funcional para:
- **Consumir** eventos de `incidencias.creadas` desde Kafka
- **Procesar** la incidencia aplicando reglas de priorización
- **Producir** eventos de `incidencias.priorizadas` hacia Kafka

### Ventajas de Spring Cloud Stream
- ✅ Abstracción del binder (Kafka, RabbitMQ, etc.)
- ✅ Programación funcional simple con `Function<Input, Output>`
- ✅ Configuración declarativa en YAML
- ✅ Menos código boilerplate
- ✅ Re-intentos y manejo de errores configurables

## Para qué sirve
- Calcular prioridad automáticamente basándose en palabras clave configurables
- Persistir prioridades calculadas en su propio modelo de datos
- Publicar eventos de priorización para el resto del sistema

## Cómo montarlo (desarrollo)
**Requisitos:** JDK 21+, Maven, Docker (para DB y Kafka)

1. Levantar infraestructura (desde la raíz del proyecto):
   ```powershell
   docker compose -f docker-compose-postresql.yml up -d postgres
   docker compose -f docker-compose-postresql.yml run --rm flyway migrate
   docker compose up -d kafka
   ```

2. Ejecutar el servicio:
   ```powershell
   mvn -f priorizacion-incidencias spring-boot:run
   ```

## Configuración

### Palabras críticas para priorización
Las palabras clave que determinan prioridad "alta" se configuran en `application.yml`:
```yaml
priorizacion:
  palabras-criticas: incendio, explosion, explosivo, heridos, urgente
  delay-segundos: 4  # Simula procesamiento (opcional)
```

### Bindings de Spring Cloud Stream
- **Input:** `incidencias.creadas` (consumer group: `priorizacion-service`)
- **Output:** `incidencias.priorizadas`

La configuración completa está en [`application.yml`](src/main/resources/application.yml).

## Modelo de datos
- Tabla: `priorizacion_incidencias.prioridad`
- Campos: `incidencia_id`, `prioridad` (enum: CRITICA, ALTA, MEDIA), `motivo`, `calculada_en`

## Funcionamiento
1. Llega un evento `IncidenciaCreadaEvent`
2. El servicio analiza la descripción buscando palabras críticas
3. Se asigna prioridad "CRITICA" si encuentra "accidente" o "grave", "alta" si encuentra otras palabras críticas configuradas, "media" en caso contrario
4. Se persiste el resultado
5. Se publica un evento `IncidenciaPriorizadaEvent`
