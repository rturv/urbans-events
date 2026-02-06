-- Migración V8: actualizar tabla de notificaciones
-- Añadir columnas necesarias y eliminar campo booleano enviado

-- Añadir columna incidencia_id si no existe
ALTER TABLE notificaciones.notificaciones 
ADD COLUMN IF NOT EXISTS incidencia_id BIGINT;

-- Añadir columna canal si no existe
ALTER TABLE notificaciones.notificaciones 
ADD COLUMN IF NOT EXISTS canal VARCHAR(50);

-- Añadir columna estado si no existe
ALTER TABLE notificaciones.notificaciones 
ADD COLUMN IF NOT EXISTS estado VARCHAR(50);

-- Actualizar registros existentes con estado NO_ENVIADA si enviado es false
UPDATE notificaciones.notificaciones 
SET estado = CASE 
    WHEN enviado = true THEN 'ENVIADA'
    ELSE 'NO_ENVIADA'
END
WHERE estado IS NULL;

-- Actualizar registros existentes con canal 'email' por defecto si es null
UPDATE notificaciones.notificaciones 
SET canal = 'email'
WHERE canal IS NULL;

-- Eliminar columna enviado
ALTER TABLE notificaciones.notificaciones 
DROP COLUMN IF EXISTS enviado;

-- Permitir NULL en destino (para notificaciones ignoradas)
ALTER TABLE notificaciones.notificaciones 
ALTER COLUMN destino DROP NOT NULL;

-- Añadir constraint NOT NULL a estado después de migrar datos
ALTER TABLE notificaciones.notificaciones 
ALTER COLUMN estado SET NOT NULL;
