/**
 * Interfaz que mapea EstadisticasTipoDTO del backend
 * Resumen de un tipo sin desglosar por prioridad
 */
export interface EstadisticasTipo {
  tipo: string;
  cantidad: number;
  cantidadResuelta?: number;
  cantidadPendiente?: number;
  cantidadRechazada?: number;
  tasaExitoPct?: number;
  tiempoPromedioResolucionSeg?: number;
  tiempoMinResolucionSeg?: number;
  tiempoMaxResolucionSeg?: number;
}
