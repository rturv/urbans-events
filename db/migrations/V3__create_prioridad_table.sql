-- Migración V3: crear tabla `prioridad` en el esquema `priorizacion_incidencias`
-- Crear secuencia por tabla (si no existe) y tabla `prioridades`
CREATE SEQUENCE IF NOT EXISTS priorizacion_incidencias.prioridades_seq START 1;

CREATE TABLE priorizacion_incidencias.prioridad_incidencia (
	incidencia_id int8 NOT NULL,
	actualizada_en timestamptz(6) NULL,
	motivo varchar(255) NULL,
	prioridad varchar(255) NULL,
	CONSTRAINT prioridad_incidencia_pkey PRIMARY KEY (incidencia_id)
);
