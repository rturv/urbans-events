-- Schema: metricas
-- Este schema contiene las tablas de métricas para el servicio metricas-quarkus
-- Las métricas se calculan en base a eventos de Kafka

-- Crear schema
CREATE SCHEMA IF NOT EXISTS metricas;

-- ============================================================================
-- Tabla: incidencias_metricas
-- Almacena el estado actual de métricas por incidencia
-- Se actualiza conforme llegan eventos de Kafka
-- ============================================================================
CREATE TABLE IF NOT EXISTS metricas.incidencias_metricas (
    id BIGSERIAL PRIMARY KEY,
    incidencia_id BIGINT NOT NULL UNIQUE,
    tipo_incidencia VARCHAR(100) NOT NULL,
    prioridad VARCHAR(50),
    estado_actual VARCHAR(50) NOT NULL DEFAULT 'PENDIENTE',
    
    -- Timestamps de eventos (UTC)
    tiempo_creacion TIMESTAMP NOT NULL,
    tiempo_priorizacion TIMESTAMP,
    tiempo_notificacion TIMESTAMP,
    tiempo_resolucion TIMESTAMP,
    
    -- Tiempos calculados en milisegundos
    ms_priorizacion BIGINT,        -- Desde creación a priorización
    ms_notificacion BIGINT,        -- Desde priorización a notificación
    ms_resolucion BIGINT,          -- Desde creación a resolución
    
    -- Bandera de estado resuelto
    es_resuelto BOOLEAN DEFAULT false,
    
    -- Control
    ultima_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Crear índices para incidencias_metricas
CREATE INDEX IF NOT EXISTS idx_incidencia_tipo ON metricas.incidencias_metricas(tipo_incidencia);
CREATE INDEX IF NOT EXISTS idx_incidencia_estado ON metricas.incidencias_metricas(estado_actual);
CREATE INDEX IF NOT EXISTS idx_incidencia_esresuelto ON metricas.incidencias_metricas(es_resuelto);
CREATE INDEX IF NOT EXISTS idx_incidencia_tipo_prioridad ON metricas.incidencias_metricas(tipo_incidencia, prioridad);

-- ============================================================================
-- Tabla: metricas_agregadas
-- Almacena resúmenes de métricas por tipo y prioridad
-- Se recalcula cada vez que llega un evento de cambio de estado
-- ============================================================================
CREATE TABLE IF NOT EXISTS metricas.metricas_agregadas (
    id BIGSERIAL PRIMARY KEY,
    tipo_incidencia VARCHAR(100) NOT NULL,
    prioridad VARCHAR(50),
    
    -- Conteos
    cantidad_total BIGINT NOT NULL DEFAULT 0,
    cantidad_resuelta BIGINT NOT NULL DEFAULT 0,
    cantidad_pendiente BIGINT NOT NULL DEFAULT 0,
    cantidad_rechazada BIGINT NOT NULL DEFAULT 0,
    
    -- Tiempos en segundos (promedios, min, max)
    tiempo_promedio_resolucion_seg DOUBLE PRECISION,
    tiempo_min_resolucion_seg BIGINT,
    tiempo_max_resolucion_seg BIGINT,
    
    -- Percentiles en segundos (para gráficas)
    percentil_50_seg DOUBLE PRECISION,  -- p50 (mediana)
    percentil_95_seg DOUBLE PRECISION,  -- p95
    percentil_99_seg DOUBLE PRECISION,  -- p99
    
    -- Tiempo promedio de priorización
    tiempo_promedio_priorizacion_seg DOUBLE PRECISION,
    
    -- Tasas en porcentaje
    tasa_exito_pct DOUBLE PRECISION,     -- % resuelta
    tasa_fracaso_pct DOUBLE PRECISION,   -- % rechazada
    tasa_pendiente_pct DOUBLE PRECISION, -- % pendiente
    
    -- Control
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraint único: tipo + prioridad no pueden repetirse
    CONSTRAINT uk_tipo_prioridad UNIQUE(tipo_incidencia, prioridad)
);

-- Crear índices para metricas_agregadas
CREATE INDEX IF NOT EXISTS idx_agregada_tipo ON metricas.metricas_agregadas(tipo_incidencia);
CREATE INDEX IF NOT EXISTS idx_agregada_tipo_prioridad ON metricas.metricas_agregadas(tipo_incidencia, prioridad);

-- ============================================================================
-- Comentarios sobre las tablas (documentación)
-- ============================================================================
COMMENT ON TABLE metricas.incidencias_metricas IS 
    'Métricas de incidencias individuales. Estado actual sin histórico.';

COMMENT ON TABLE metricas.metricas_agregadas IS 
    'Métricas agregadas por tipo y prioridad. Se recalcula cuando cambia una incidencia.';

COMMENT ON COLUMN metricas.incidencias_metricas.estado_actual IS 
    'Estados posibles: PENDIENTE, RESUELTO, CERRADO, RECHAZADO';

COMMENT ON COLUMN metricas.incidencias_metricas.ms_resolucion IS 
    'Tiempo en milisegundos desde creación hasta resolución';

COMMENT ON COLUMN metricas.metricas_agregadas.percentil_50_seg IS 
    'Mediana de tiempo de resolución en segundos (p50)';
