CREATE SCHEMA IF NOT EXISTS metricas;


CREATE TABLE IF NOT EXISTS metricas.incidencias_metricas (
	id int8 NOT NULL,
	esresuelto bool NULL,
	estado_actual varchar(255) NULL,
	incidencia_id int8 NOT NULL,
	ms_notificacion int8 NULL,
	ms_priorizacion int8 NULL,
	ms_resolucion int8 NULL,
	prioridad varchar(255) NULL,
	tiempo_creacion timestamptz(6) NOT NULL,
	tiempo_notificacion timestamptz(6) NULL,
	tiempo_priorizacion timestamptz(6) NULL,
	tiempo_resolucion timestamptz(6) NULL,
	tipo_incidencia varchar(255) NOT NULL,
	ultima_actualizacion timestamptz(6) NOT NULL,
	CONSTRAINT incidencias_metricas_estado_actual_check CHECK (((estado_actual)::text = ANY ((ARRAY['PENDIENTE'::character varying, 'RESUELTO'::character varying, 'CERRADO'::character varying, 'RECHAZADO'::character varying])::text[]))),
	CONSTRAINT incidencias_metricas_incidencia_id_key UNIQUE (incidencia_id),
	CONSTRAINT incidencias_metricas_pkey PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS metricas.metricas_agregadas (
	id int8 NOT NULL,
	cantidad_pendiente int8 NOT NULL,
	cantidad_rechazada int8 NOT NULL,
	cantidad_resuelta int8 NOT NULL,
	cantidad_total int8 NOT NULL,
	fecha_actualizacion timestamptz(6) NOT NULL,
	percentil_50_seg float8 NULL,
	percentil_95_seg float8 NULL,
	percentil_99_seg float8 NULL,
	prioridad varchar(255) NULL,
	tasa_exito_pct float8 NULL,
	tasa_fracaso_pct float8 NULL,
	tasa_pendiente_pct float8 NULL,
	tiempo_max_resolucion_seg int8 NULL,
	tiempo_min_resolucion_seg int8 NULL,
	tiempo_promedio_priorizacion_seg float8 NULL,
	tiempo_promedio_resolucion_seg float8 NULL,
	tipo_incidencia varchar(255) NOT NULL,
	CONSTRAINT metricas_agregadas_pkey PRIMARY KEY (id),
	CONSTRAINT metricas_agregadas_tipo_incidencia_prioridad_key UNIQUE (tipo_incidencia, prioridad),
	CONSTRAINT metricas_tipo_prioridad UNIQUE (tipo_incidencia, prioridad)
);

CREATE SEQUENCE IF NOT EXISTS metricas.incidencias_metricas_seq   START WITH 1;
CREATE SEQUENCE IF NOT EXISTS metricas.metricas_agregadas_seq   START WITH 1;