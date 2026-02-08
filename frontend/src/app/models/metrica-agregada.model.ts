/**
 * Interfaz que mapea MetricaAgregadaDTO del backend
 * Contiene métricas agregadas por tipo y prioridad
 */
export interface MetricaAgregada {
  tipoIncidencia: string;
  prioridad?: string;
  cantidadTotal: number;
  cantidadResuelta?: number;
  cantidadPendiente?: number;
  cantidadRechazada?: number;
  tasaExitoPct?: number;
  tasaFracasoPct?: number;
  tasaPendientePct?: number;
  tiempoPromedioResolucionSeg?: number;
  tiempoMinResolucionSeg?: number;
  tiempoMaxResolucionSeg?: number;
  p50Seg?: number;
  p95Seg?: number;
  p99Seg?: number;
  tiempoPromedioProblemaizacionSeg?: number;
}
