/**
 * Tipos y enumeraciones para el módulo de métricas
 */

export enum TipoIncidencia {
  BASURA = 'BASURA',
  OBRAS = 'OBRAS',
  ALUMBRADO = 'ALUMBRADO',
  SEGURIDAD = 'SEGURIDAD',
  SANIDAD = 'SANIDAD'
}

export enum Prioridad {
  ALTA = 'ALTA',
  MEDIA = 'MEDIA',
  BAJA = 'BAJA'
}

export enum EstadoIncidencia {
  PENDIENTE = 'PENDIENTE',
  RESUELTO = 'RESUELTO',
  CERRADO = 'CERRADO',
  RECHAZADO = 'RECHAZADO'
}

/**
 * Estado del filtro en el dashboard
 */
export interface FilterState {
  tipo?: string;
  prioridad?: string;
}

/**
 * Estado de error para mostrar en banner
 */
export interface ErrorState {
  hasError: boolean;
  message: string;
  timestamp: Date;
}

/**
 * Opciones de configuración para el refresh automático
 */
export interface RefreshConfig {
  enabled: boolean;
  interval: number; // en milisegundos
}
