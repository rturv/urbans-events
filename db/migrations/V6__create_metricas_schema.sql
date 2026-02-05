-- Migración V6: crear esquema metricas y tablas
CREATE SCHEMA IF NOT EXISTS metricas;

-- Crear secuencia y tabla para indicadores
CREATE SEQUENCE IF NOT EXISTS metricas.indicadores_seq START 1;

CREATE TABLE IF NOT EXISTS metricas.indicadores (
  id BIGINT PRIMARY KEY DEFAULT nextval('metricas.indicadores_seq'),
  tipo VARCHAR(255),
  total BIGINT DEFAULT 0,
  prioridad_alta BIGINT DEFAULT 0,
  prioridad_media BIGINT DEFAULT 0,
  prioridad_baja BIGINT DEFAULT 0
);

-- Crear secuencia y tabla para resumen_notificaciones
CREATE SEQUENCE IF NOT EXISTS metricas.resumen_notificaciones_seq START 1;

CREATE TABLE IF NOT EXISTS metricas.resumen_notificaciones (
  id BIGINT PRIMARY KEY DEFAULT nextval('metricas.resumen_notificaciones_seq'),
  total BIGINT DEFAULT 0
);

-- Fin de migración V6