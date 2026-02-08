/**
 * Utilidades para formateo de números y tiempos
 */

/**
 * Formatea un número para mostrar con separadores de miles
 */
export function formatearNumero(valor: number | null | undefined): string {
  if (valor === null || valor === undefined) return '—';
  return valor.toLocaleString('es-ES');
}

/**
 * Formatea un porcentaje con 1 decimal
 */
export function formatearPorcentaje(valor: number | null | undefined): string {
  if (valor === null || valor === undefined) return '—';
  return `${valor.toFixed(1)}%`;
}

/**
 * Convierte segundos a un formato legible (ej: "1h 23m 45s" o "5m 30s")
 */
export function formatearTiempo(segundos: number | null | undefined): string {
  if (segundos === null || segundos === undefined) return '—';
  if (segundos === 0) return '0s';

  const horas = Math.floor(segundos / 3600);
  const minutos = Math.floor((segundos % 3600) / 60);
  const segs = Math.floor(segundos % 60);

  const partes: string[] = [];
  if (horas > 0) partes.push(`${horas}h`);
  if (minutos > 0) partes.push(`${minutos}m`);
  if (segs > 0 || partes.length === 0) partes.push(`${segs}s`);

  return partes.join(' ');
}

/**
 * Convierte minutos a "Xm" o "Xh Ym"
 */
export function formatearMinutos(minutos: number | null | undefined): string {
  if (minutos === null || minutos === undefined) return '—';
  if (minutos < 60) return `${minutos}m`;

  const horas = Math.floor(minutos / 60);
  const mins = minutos % 60;
  return `${horas}h ${mins}m`;
}

/**
 * Formatea un número para mostrar como métrica pequeña (K, M, etc)
 */
export function formatearCompacto(valor: number): string {
  if (valor >= 1_000_000) return `${(valor / 1_000_000).toFixed(1)}M`;
  if (valor >= 1_000) return `${(valor / 1_000).toFixed(1)}K`;
  return valor.toString();
}

/**
 * Obtiene el nombre legible de un tipo de incidencia
 */
export const TIPO_INCIDENCIA_LABELS: Record<string, string> = {
  BASURA: 'Basura',
  OBRAS: 'Obras',
  ALUMBRADO: 'Alumbrado',
  SEGURIDAD: 'Seguridad',
  SANIDAD: 'Sanidad',
  OTRO: 'Otro'
};

export function obtenerLabelTipo(tipo: string): string {
  return TIPO_INCIDENCIA_LABELS[tipo] || tipo;
}

/**
 * Obtiene el nombre legible de una prioridad
 */
export const PRIORIDAD_LABELS: Record<string, string> = {
  ALTA: 'Alta',
  MEDIA: 'Media',
  BAJA: 'Baja'
};

export function obtenerLabelPrioridad(prioridad: string): string {
  return PRIORIDAD_LABELS[prioridad] || prioridad;
}

/**
 * Obtiene el nombre legible de un estado
 */
export const ESTADO_LABELS: Record<string, string> = {
  PENDIENTE: 'Pendiente',
  RESUELTO: 'Resuelto',
  CERRADO: 'Cerrado',
  RECHAZADO: 'Rechazado'
};

export function obtenerLabelEstado(estado: string): string {
  return ESTADO_LABELS[estado] || estado;
}
