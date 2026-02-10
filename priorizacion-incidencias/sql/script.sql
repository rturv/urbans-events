CREATE SCHEMA IF NOT EXISTS priorizacion_incidencias;

CREATE TABLE IF NOT EXISTS priorizacion_incidencias.prioridad_incidencia (
	incidencia_id int8 NOT NULL,
	actualizada_en timestamptz(6) NULL,
	motivo varchar(255) NULL,
	prioridad varchar(255) NULL,
	CONSTRAINT prioridad_incidencia_pkey PRIMARY KEY (incidencia_id)
);


CREATE SEQUENCE IF NOT EXISTS priorizacion_incidencias.prioridades_seq START WITH 1;