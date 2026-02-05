-- Migración V2: crear tabla `incidencias` en el esquema `registro_incidencias`
-- Crear secuencia por tabla (si no existe)
CREATE SEQUENCE IF NOT EXISTS registro_incidencias.incidencias_seq START 1;

CREATE TABLE IF NOT EXISTS registro_incidencias.incidencias (
  id BIGINT PRIMARY KEY DEFAULT nextval('registro_incidencias.incidencias_seq'),
  tipo VARCHAR(100) NOT NULL,
  descripcion TEXT,
  origen VARCHAR(255),
  ubicacion VARCHAR(255),
  estado VARCHAR(50),
  creada_en TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Índices recomendados (descomentar si se necesitan)
-- CREATE INDEX IF NOT EXISTS idx_incidencias_tipo ON registro_incidencias.incidencias(tipo);
-- CREATE INDEX IF NOT EXISTS idx_incidencias_creada_en ON registro_incidencias.incidencias(creada_en);
