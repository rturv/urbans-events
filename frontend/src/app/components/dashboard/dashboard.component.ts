import {
  Component,
  OnInit,
  OnDestroy,
  ChangeDetectorRef
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { interval, Subject, Observable } from 'rxjs';
import { takeUntil, switchMap } from 'rxjs/operators';

import { MetricasService } from '../../services/metricas.service';
import { ConfigService } from '../../services/config.service';
import { ErrorBannerComponent } from '../error-banner/error-banner.component';
import { HeaderComponent } from '../header/header.component';
import { KpiCardComponent, TablaPendientesComponent } from '../widgets';
import {
  ResumenMetricas,
  MetricaAgregada,
  EstadisticasTipo,
  IncidenciaPendiente
} from '../../models';
import { ErrorState } from '../../types/metricas.types';

/**
 * Componente contenedor principal del dashboard
 * Maneja la lógica de carga de datos, refresh automático y estado global
 * OnPush ChangeDetection para optimizar performance
 */
@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    ErrorBannerComponent,
    HeaderComponent,
    KpiCardComponent,
    TablaPendientesComponent
  ],
  template: `
    <div class="dashboard">
      <app-error-banner [error]="errorState"></app-error-banner>

      <app-header
        [status]="status"
        [lastUpdated]="lastUpdated"
        (click)="onRefresh()"
      ></app-header>

      <main class="dashboard__content">
        <!-- KPI Cards -->
        <div class="dashboard__kpi">
          <app-kpi-card [data]="resumen"></app-kpi-card>
        </div>

        <!-- Gráficos principales -->
        <div class="dashboard__charts">
          <!-- Resumen de Estados -->
          <div class="dashboard__summary-card">
            <h3>Distribución por Estado</h3>
            <div class="summary-stats">
              <div class="stat">
                <span class="stat-label">Resuelto:</span>
                <span class="stat-value" style="color: #28a745">{{ resumen?.incidenciasResueltas ?? 0 }}</span>
              </div>
              <div class="stat">
                <span class="stat-label">Pendiente:</span>
                <span class="stat-value" style="color: #ffc107">{{ resumen?.incidenciasPendientes ?? 0 }}</span>
              </div>
              <div class="stat">
                <span class="stat-label">Rechazado:</span>
                <span class="stat-value" style="color: #dc3545">{{ resumen?.incidenciasRechazadas ?? 0 }}</span>
              </div>
            </div>
          </div>

          <!-- Resumen por Tipo -->
          <div class="dashboard__summary-card">
            <h3>Incidencias por Tipo</h3>
            <div class="summary-stats">
              <div *ngFor="let estadistica of estadisticasTipo" class="stat">
                <span class="stat-label">{{ estadistica.tipo }}:</span>
                <span class="stat-value">{{ estadistica.cantidad }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Tabla de pendientes -->
        <div class="dashboard__table">
          <app-tabla-pendientes [data]="pendientes"></app-tabla-pendientes>
        </div>
      </main>
    </div>
  `,
  styles: [`
    .dashboard {
      min-height: 100vh;
      background: var(--surface-0);
    }

    .dashboard__content {
      max-width: 1400px;
      margin: 0 auto;
      padding: 2rem;
    }

    .dashboard__kpi {
      margin-bottom: 2rem;
      display: grid;
      grid-template-columns: 1fr;
      gap: var(--grid-gap);
    }

    .dashboard__charts {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
      gap: var(--grid-gap);
      margin-bottom: 2rem;
    }

    .dashboard__summary-card {
      background: white;
      border-radius: 8px;
      padding: 1.5rem;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    }

    .dashboard__summary-card h3 {
      margin: 0 0 1rem 0;
      font-size: 1.1rem;
      font-weight: 600;
      color: var(--ink-500);
    }

    .summary-stats {
      display: flex;
      flex-direction: column;
      gap: 0.75rem;
    }

    .stat {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 0.5rem 0;
      border-bottom: 1px solid #e9ecef;
    }

    .stat:last-child {
      border-bottom: none;
    }

    .stat-label {
      font-weight: 500;
      color: var(--ink-500);
    }

    .stat-value {
      font-weight: 600;
      font-size: 1.1rem;
    }

    .dashboard__table {
      margin-bottom: 2rem;
    }

    @media (max-width: 1200px) {
      .dashboard__content {
        padding: 1.5rem;
      }

      .dashboard__charts {
        grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
      }
    }

    @media (max-width: 768px) {
      .dashboard__content {
        padding: 1rem;
      }

      .dashboard__charts {
        grid-template-columns: 1fr;
      }
    }

    @media (max-width: 640px) {
      .dashboard__content {
        padding: 0.75rem;
      }

      .dashboard__kpi {
        margin-bottom: 1.5rem;
      }

      .dashboard__charts {
        gap: 0.75rem;
        margin-bottom: 1.5rem;
      }

      .dashboard__table {
        margin-bottom: 1rem;
      }
    }
  `]
})
export class DashboardComponent implements OnInit, OnDestroy {
  status: 'loading' | 'ready' | 'error' = 'loading';
  lastUpdated: string | null = null;
  errorState: ErrorState = {
    hasError: false,
    message: '',
    timestamp: new Date()
  };

  // Datos del dashboard
  resumen: ResumenMetricas | null = null;
  agregadas: MetricaAgregada[] = [];
  estadisticasTipo: EstadisticasTipo[] = [];
  pendientes: IncidenciaPendiente[] = [];

  // Control de refresh
  private destroy$ = new Subject<void>();
  private refreshInterval$!: Observable<number>;

  constructor(
    private metricas: MetricasService,
    private config: ConfigService,
    private cdr: ChangeDetectorRef
  ) {
    this.refreshInterval$ = interval(this.config.refreshInterval);
  }

  ngOnInit(): void {
    this.cargarDatos();
    this.setupAutoRefresh();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

   /**
    * Carga todos los datos del dashboard
    */
   private cargarDatos(): void {
     this.status = 'loading';
     this.errorState.hasError = false;
     this.cdr.markForCheck();

     // Cargar resumen
     this.metricas.obtenerResumen()
       .pipe(takeUntil(this.destroy$))
       .subscribe({
         next: (data) => {
           this.resumen = data;
           this.actualizarTimestamp();
           this.status = 'ready';
           this.cdr.markForCheck();
         },
         error: (err) => {
           this.mostrarError(err.message);
           this.status = 'error';
           this.cdr.markForCheck();
         }
       });

     // Cargar agregadas
     this.metricas
       .obtenerAgregadas()
       .pipe(takeUntil(this.destroy$))
       .subscribe({
         next: (data) => {
           this.agregadas = data;
           this.cdr.markForCheck();
         },
         error: (err) => {
           console.error('Error cargando agregadas', err);
         }
       });

     // Cargar estadísticas por tipo
     this.metricas.obtenerEstadisticasPorTipo()
       .pipe(takeUntil(this.destroy$))
       .subscribe({
         next: (data) => {
           this.estadisticasTipo = data;
           this.cdr.markForCheck();
         },
         error: (err) => {
           console.error('Error cargando estadísticas por tipo', err);
         }
       });

     // Cargar incidencias pendientes
     this.metricas.obtenerPendientes()
       .pipe(takeUntil(this.destroy$))
       .subscribe({
         next: (data) => {
           this.pendientes = data;
           this.cdr.markForCheck();
         },
         error: (err) => {
           console.error('Error cargando pendientes', err);
         }
       });
   }

  /**
   * Configura el auto-refresh cada 60 segundos
   */
  private setupAutoRefresh(): void {
    this.refreshInterval$
      .pipe(
        switchMap(() => this.metricas.obtenerResumen()),
        takeUntil(this.destroy$)
      )
      .subscribe({
        next: (data) => {
          this.resumen = data;
          this.actualizarTimestamp();
          this.status = 'ready';
          this.cdr.markForCheck();
        },
        error: (err) => {
          console.error('Error en auto-refresh', err);
          this.status = 'error';
          this.cdr.markForCheck();
        }
      });
  }

  /**
   * Actualiza el timestamp de última actualización
   */
  private actualizarTimestamp(): void {
    const ahora = new Date();
    const horas = String(ahora.getHours()).padStart(2, '0');
    const minutos = String(ahora.getMinutes()).padStart(2, '0');
    const segundos = String(ahora.getSeconds()).padStart(2, '0');
    this.lastUpdated = `${horas}:${minutos}:${segundos}`;
  }

  /**
   * Muestra un error en el banner
   */
  private mostrarError(mensaje: string): void {
    this.errorState = {
      hasError: true,
      message: mensaje,
      timestamp: new Date()
    };
  }

  /**
   * Refresh manual - limpia caché y recarga datos
   */
  onRefresh(): void {
    this.metricas.clearCache();
    this.cargarDatos();
  }
}
