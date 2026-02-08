/**
 * Interfaz que mapea IncidenciaPendienteDTO del backend
 * Incidencias pendientes activas con tiempo transcurrido
 */
export interface IncidenciaPendiente {
  incidenciaId: number;
  tipo: string;
  prioridad?: string;
  estado: string;
  tiempoDesdeCreacionSeg?: number;
  tiempoDesdeCreacionMin?: number;
}
