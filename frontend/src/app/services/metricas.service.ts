import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';

import { ConfigService } from './config.service';
import {
  ResumenMetricas,
  MetricaAgregada,
  EstadisticasTipo,
  IncidenciaPendiente,
  MetricaIncidencia
} from '../models';
import { FilterState } from '../types/metricas.types';

/**
 * Servicio de métricas
 * Maneja todas las llamadas HTTP hacia el backend
 * Implementa caché de 60 segundos para optimizar
 */
@Injectable({
  providedIn: 'root'
})
export class MetricasService {
  // Almacenamiento en caché: key -> { data, timestamp }
  private cache = new Map<string, { data: any; timestamp: number }>();
  private readonly cacheTimeout = 60000; // 60 segundos

  constructor(
    private http: HttpClient,
    private config: ConfigService
  ) {}

  /**
   * Obtiene el resumen general de métricas
   * GET /api/metricas/resumen
   */
  obtenerResumen(): Observable<ResumenMetricas> {
    return this.getWithCache<ResumenMetricas>('resumen', () =>
      this.http.get<ResumenMetricas>(`${this.config.apiBaseUrl}/resumen`)
    );
  }

  /**
   * Obtiene métricas agregadas con filtros opcionales
   * GET /api/metricas/agregadas?tipo=X&prioridad=Y
   */
  obtenerAgregadas(
    tipo?: string,
    prioridad?: string
  ): Observable<MetricaAgregada[]> {
    const cacheKey = `agregadas:${tipo || 'all'}:${prioridad || 'all'}`;
    let url = `${this.config.apiBaseUrl}/agregadas`;

    const params: string[] = [];
    if (tipo) params.push(`tipo=${encodeURIComponent(tipo)}`);
    if (prioridad) params.push(`prioridad=${encodeURIComponent(prioridad)}`);

    if (params.length > 0) {
      url += `?${params.join('&')}`;
    }

    return this.getWithCache<MetricaAgregada[]>(cacheKey, () =>
      this.http.get<MetricaAgregada[]>(url)
    );
  }

  /**
   * Obtiene métricas para un tipo específico
   * GET /api/metricas/tipo/{tipo}
   */
  obtenerPorTipo(tipo: string): Observable<MetricaAgregada> {
    const cacheKey = `tipo:${tipo}`;
    return this.getWithCache<MetricaAgregada>(cacheKey, () =>
      this.http.get<MetricaAgregada>(
        `${this.config.apiBaseUrl}/tipo/${encodeURIComponent(tipo)}`
      )
    );
  }

  /**
   * Obtiene métricas de una incidencia individual
   * GET /api/metricas/incidencia/{id}
   */
  obtenerIncidencia(id: number): Observable<MetricaIncidencia> {
    const cacheKey = `incidencia:${id}`;
    return this.getWithCache<MetricaIncidencia>(cacheKey, () =>
      this.http.get<MetricaIncidencia>(
        `${this.config.apiBaseUrl}/incidencia/${id}`
      )
    );
  }

  /**
   * Obtiene estadísticas agrupadas por tipo
   * GET /api/metricas/estadisticas-por-tipo
   */
  obtenerEstadisticasPorTipo(): Observable<EstadisticasTipo[]> {
    return this.getWithCache<EstadisticasTipo[]>(
      'estadisticas-por-tipo',
      () =>
        this.http.get<EstadisticasTipo[]>(
          `${this.config.apiBaseUrl}/estadisticas-por-tipo`
        )
    );
  }

  /**
   * Obtiene incidencias pendientes activas
   * GET /api/metricas/pendientes
   */
  obtenerPendientes(): Observable<IncidenciaPendiente[]> {
    // No cacheamos pendientes porque cambian frecuentemente
    return this.http
      .get<IncidenciaPendiente[]>(
        `${this.config.apiBaseUrl}/pendientes`
      )
      .pipe(catchError(this.handleError));
  }

  /**
   * Limpia todo el caché
   * Útil cuando el usuario hace clic en "Actualizar"
   */
  clearCache(): void {
    this.cache.clear();
  }

  /**
   * Limpia una entrada específica del caché
   */
  clearCacheKey(key: string): void {
    this.cache.delete(key);
  }

  /**
   * Método privado para obtener con caché
   * @param key - Clave del caché
   * @param fetcher - Función que realiza la llamada HTTP
   */
  private getWithCache<T>(
    key: string,
    fetcher: () => Observable<T>
  ): Observable<T> {
    const cached = this.getCachedData(key);

    if (cached !== null) {
      // Retornar desde caché
      return new Observable((observer) => {
        observer.next(cached as T);
        observer.complete();
      });
    }

    // Si no hay caché, hacer la llamada
    return fetcher().pipe(
      tap((data) => this.setCachedData(key, data)),
      catchError(this.handleError)
    );
  }

  /**
   * Obtiene datos del caché si están válidos
   */
  private getCachedData(key: string): any | null {
    const cached = this.cache.get(key);

    if (!cached) {
      return null;
    }

    if (!this.isCacheValid(cached.timestamp)) {
      this.cache.delete(key);
      return null;
    }

    return cached.data;
  }

  /**
   * Almacena datos en el caché
   */
  private setCachedData(key: string, data: any): void {
    this.cache.set(key, {
      data,
      timestamp: Date.now()
    });
  }

  /**
   * Verifica si el caché sigue siendo válido
   */
  private isCacheValid(timestamp: number): boolean {
    return Date.now() - timestamp < this.cacheTimeout;
  }

  /**
   * Manejo de errores HTTP
   */
  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'Error desconocido al conectar con el servidor';

    if (error.error instanceof ErrorEvent) {
      // Error del cliente
      errorMessage = `Error: ${error.error.message}`;
    } else {
      // Error del servidor
      switch (error.status) {
        case 0:
          errorMessage = 'Error de conexión. Verifica que el servidor está disponible.';
          break;
        case 400:
          errorMessage = 'Solicitud inválida';
          break;
        case 404:
          errorMessage = 'Recurso no encontrado';
          break;
        case 500:
          errorMessage = 'Error del servidor. Intenta más tarde.';
          break;
        case 503:
          errorMessage = 'Servicio no disponible. Intenta más tarde.';
          break;
        default:
          errorMessage = `Error HTTP ${error.status}: ${error.statusText}`;
      }
    }

    console.error('[MetricasService]', errorMessage, error);
    return throwError(() => ({
      message: errorMessage,
      status: error.status,
      timestamp: new Date()
    }));
  }
}
