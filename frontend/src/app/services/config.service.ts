import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';

/**
 * Servicio de configuración global
 * Lee valores desde environment.ts y los expone de manera centralizada
 */
@Injectable({
  providedIn: 'root'
})
export class ConfigService {
  /**
   * URL base para todos los endpoints de métricas
   */
  get apiBaseUrl(): string {
    return environment.apiBaseUrl;
  }

  /**
   * Intervalo de auto-refresh en milisegundos
   */
  get refreshInterval(): number {
    return environment.refreshInterval;
  }

  /**
   * Indica si estamos en modo producción
   */
  get isProduction(): boolean {
    return environment.production;
  }
}
