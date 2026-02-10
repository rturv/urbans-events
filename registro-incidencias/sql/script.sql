CREATE SCHEMA IF NOT EXISTS registro_incidencias;


CREATE TABLE IF NOT EXISTS registro_incidencias.incidencia_comentarios (
	id bigserial NOT NULL,
	incidencia_id int8 NOT NULL,
	comentario varchar(255) NOT NULL,
	CONSTRAINT incidencia_comentarios_pkey PRIMARY KEY (id),
	CONSTRAINT fk_incidencia_comentarios_incidencia FOREIGN KEY (incidencia_id) REFERENCES registro_incidencias.incidencias(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_incidencia_comentarios_incidencia_id ON registro_incidencias.incidencia_comentarios USING btree (incidencia_id);

CREATE TABLE IF NOT EXISTS registro_incidencias.incidencias (
	id int8 DEFAULT nextval('registro_incidencias.incidencias_seq'::regclass) NOT NULL,
	tipo varchar(100) NOT NULL,
	descripcion varchar(2000) NULL,
	origen varchar(255) NULL,
	ubicacion varchar(255) NULL,
	estado varchar(50) NULL,
	creada_en timestamptz DEFAULT now() NULL,
	prioridad varchar(50) DEFAULT NULL::character varying NULL,
	CONSTRAINT incidencias_pkey PRIMARY KEY (id)
);


CREATE SEQUENCE IF NOT EXISTS registro_incidencias.incidencias_seq START 1;
CREATE SEQUENCE IF NOT EXISTS registro_incidencias.incidencia_comentarios_id_seq START 1;
