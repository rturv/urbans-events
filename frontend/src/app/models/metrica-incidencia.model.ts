/**
 * Interfaz que mapea MetricaIncidenciaDTO del backend
 * Métricas detalladas de una incidencia individual
 */
export interface MetricaIncidencia {
  incidenciaId: number;
  tipo: string;
  prioridad?: string;
  estado: string;
  tiempoCreacionPriorizacionMs?: number;
  tiempoCreacionPriorizacionSeg?: number;
  tiempoCreacionNotificacionMs?: number;
  tiempoCreacionNotificacionSeg?: number;
  tiempoCreacionResolucionMs?: number;
  tiempoCreacionResolucionSeg?: number;
  tiempoCreacionCierreMs?: number;
  tiempoCreacionCierreSeg?: number;
}
