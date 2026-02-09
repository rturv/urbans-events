-- Migración V10: crear tabla incidencia_comentarios para los comentarios de las incidencias
CREATE TABLE IF NOT EXISTS registro_incidencias.incidencia_comentarios (
    id BIGSERIAL PRIMARY KEY,
    incidencia_id BIGINT NOT NULL,
    comentario VARCHAR(2000) NOT NULL,
    CONSTRAINT fk_incidencia_comentarios_incidencia FOREIGN KEY (incidencia_id)
        REFERENCES registro_incidencias.incidencias(id) ON DELETE CASCADE
);

-- Índice para búsqueda rápida por incidencia
CREATE INDEX IF NOT EXISTS idx_incidencia_comentarios_incidencia_id ON registro_incidencias.incidencia_comentarios(incidencia_id);


