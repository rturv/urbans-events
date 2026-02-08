/**
 * Interfaz que mapea ResumenMetricasDTO del backend
 * Contiene totales y tasas agregadas de todas las incidencias
 */
export interface ResumenMetricas {
  totalIncidencias: number;
  incidenciasResueltas: number;
  incidenciasPendientes: number;
  incidenciasRechazadas: number;
  tasaExitoPct: number;
  tasaFracasoPct: number;
  tasaPendientePct: number;
  tiempoPromedioResolucionSeg: number;
  tiempoPromedioResolucionMin: number;
}
