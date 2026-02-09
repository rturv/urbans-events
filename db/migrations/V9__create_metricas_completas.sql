-- Migración V9: Crear tablas de métricas completas
-- Migración V9: Crear tablas usadas por metricas-quarkus

-- Crear esquema
CREATE SCHEMA IF NOT EXISTS metricas;

-- Tabla: metricas.metricas_agregadas
CREATE TABLE IF NOT EXISTS metricas.metricas_agregadas (
    id BIGSERIAL PRIMARY KEY,
    tipo_incidencia VARCHAR(255) NOT NULL,
    prioridad VARCHAR(255),
    cantidad_total BIGINT DEFAULT 0,
    cantidad_resuelta BIGINT DEFAULT 0,
    cantidad_pendiente BIGINT DEFAULT 0,
    cantidad_rechazada BIGINT DEFAULT 0,
    tiempo_promedio_resolucion_seg DOUBLE PRECISION,
    tiempo_min_resolucion_seg BIGINT,
    tiempo_max_resolucion_seg BIGINT,
    percentil50_seg DOUBLE PRECISION,
    percentil95_seg DOUBLE PRECISION,
    percentil99_seg DOUBLE PRECISION,
    tiempo_promedio_problemaizacion_seg DOUBLE PRECISION,
    tasa_exito_pct DOUBLE PRECISION,
    tasa_fracaso_pct DOUBLE PRECISION,
    tasa_pendiente_pct DOUBLE PRECISION,
    fecha_actualizacion TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_metricas_agregadas UNIQUE (tipo_incidencia, prioridad)
);

-- Tabla: metricas.incidencias_metricas
CREATE TABLE IF NOT EXISTS metricas.incidencias_metricas (
    id BIGSERIAL PRIMARY KEY,
    incidencia_id BIGINT UNIQUE NOT NULL,
    tipo_incidencia VARCHAR(255) NOT NULL,
    prioridad VARCHAR(255),
    estado_actual VARCHAR(50),
    tiempo_creacion TIMESTAMP WITH TIME ZONE NOT NULL,
    tiempo_priorizacion TIMESTAMP WITH TIME ZONE,
    tiempo_notificacion TIMESTAMP WITH TIME ZONE,
    tiempo_resolucion TIMESTAMP WITH TIME ZONE,
    ms_prorizacion BIGINT,
    ms_notificacion BIGINT,
    ms_resolucion BIGINT,
    es_resuelto BOOLEAN DEFAULT FALSE,
    ultima_actualizacion TIMESTAMP WITH TIME ZONE NOT NULL
);

-- Índices para consultas frecuentes
CREATE INDEX IF NOT EXISTS idx_metricas_agregadas_tipo ON metricas.metricas_agregadas(tipo_incidencia);
CREATE INDEX IF NOT EXISTS idx_metricas_agregadas_prioridad ON metricas.metricas_agregadas(prioridad);
CREATE INDEX IF NOT EXISTS idx_incidencias_metricas_incidencia_id ON metricas.incidencias_metricas(incidencia_id);
CREATE INDEX IF NOT EXISTS idx_incidencias_metricas_tipo ON metricas.incidencias_metricas(tipo_incidencia);
CREATE INDEX IF NOT EXISTS idx_incidencias_metricas_estado ON metricas.incidencias_metricas(estado_actual);

CREATE SEQUENCE IF NOT EXISTS metricas.incidencias_metricas_seq START 1;

CREATE SEQUENCE IF NOT EXISTS metricas.metricas_agregadas_seq START 1;