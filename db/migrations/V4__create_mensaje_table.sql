-- Migración V4: crear tabla `mensaje` en el esquema `notificaciones`
-- Crear secuencia por tabla (si no existe) y tabla `notificaciones`
CREATE SEQUENCE IF NOT EXISTS notificaciones.notificaciones_seq START 1;

CREATE TABLE IF NOT EXISTS notificaciones.notificaciones (
  id BIGINT PRIMARY KEY DEFAULT nextval('notificaciones.notificaciones_seq'),
  destino VARCHAR(255) NOT NULL,
  contenido TEXT,
  enviado BOOLEAN DEFAULT false,
  creado TIMESTAMP WITH TIME ZONE DEFAULT now()
);
