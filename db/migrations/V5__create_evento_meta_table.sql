-- Migración V5: crear tabla `evento_meta` en el esquema `shared_events`
-- Crear secuencia por tabla (si no existe) y tabla `evento_meta`
CREATE SEQUENCE IF NOT EXISTS shared_events.evento_meta_seq START 1;

CREATE TABLE IF NOT EXISTS shared_events.evento_meta (
  id BIGINT PRIMARY KEY DEFAULT nextval('shared_events.evento_meta_seq'),
  event_id INTEGER,
  meta JSONB
);
