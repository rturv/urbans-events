CREATE SCHEMA IF NOT EXISTS notificaciones;
CREATE TABLE IF  NOT EXISTS notificaciones.notificaciones (
	id int8 DEFAULT nextval('notificaciones.notificaciones_seq'::regclass) NOT NULL,
	destino varchar(255) NULL,
	contenido varchar(4000) NULL,
	creado timestamptz DEFAULT now() NULL,
	incidencia_id int8 NULL,
	canal varchar(50) NULL,
	estado varchar(50) NOT NULL,
	CONSTRAINT notificaciones_pkey PRIMARY KEY (id)
);

CREATE SEQUENCE IF NOT EXISTS notificaciones.notificaciones_seq START WITH 1;