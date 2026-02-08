import {
  Component,
  OnInit,
  OnDestroy,
  ChangeDetectionStrategy,
  ChangeDetectorRef
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { interval, Subject, Observable } from 'rxjs';
import { takeUntil, switchMap } from 'rxjs/operators';

import { MetricasService } from '../../services/metricas.service';
import { ConfigService } from '../../services/config.service';
import { ErrorBannerComponent } from '../error-banner/error-banner.component';
import { HeaderComponent } from '../header/header.component';
import {
  KpiCardComponent,
  ChartEstadosComponent,
  ChartTiposComponent,
  ChartTasasComponent,
  ChartTiemposComponent,
  ChartBubbleComponent,
  ChartPercentilesComponent,
  ChartPriorizacionComponent,
  TablaPendientesComponent,
  FiltrosComponent
} from '../widgets';
import {
  ResumenMetricas,
  MetricaAgregada,
  EstadisticasTipo,
  IncidenciaPendiente
} from '../../models';
import { ErrorState, FilterState } from '../../types/metricas.types';

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
    ChartEstadosComponent,
    ChartTiposComponent,
    ChartTasasComponent,
    ChartTiemposComponent,
    ChartBubbleComponent,
    ChartPercentilesComponent,
    ChartPriorizacionComponent,
    TablaPendientesComponent,
    FiltrosComponent
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
        <!-- Filtros -->
        <div class="dashboard__filters">
          <app-filtros
            (filtrosChange)="onFiltrosChange($event)"
          ></app-filtros>
        </div>

        <!-- KPI Cards -->
        <div class="dashboard__kpi">
          <app-kpi-card [data]="resumen"></app-kpi-card>
        </div>

        <!-- Gráficos principales -->
        <div class="dashboard__charts">
          <app-chart-estados [data]="resumen"></app-chart-estados>
          <app-chart-tipos [data]="estadisticasTipo"></app-chart-tipos>
          <app-chart-tasas [data]="agregadas"></app-chart-tasas>
          <app-chart-tiempos [data]="agregadas"></app-chart-tiempos>
          <app-chart-bubble [data]="agregadas"></app-chart-bubble>
          <app-chart-percentiles [data]="agregadas"></app-chart-percentiles>
          <app-chart-priorizacion [data]="resumen"></app-chart-priorizacion>
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

    .dashboard__filters {
      margin-bottom: 2rem;
      display: grid;
      grid-template-columns: 1fr;
      gap: var(--grid-gap);
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
  `],
  changeDetection: ChangeDetectionStrategy.OnPush
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

  // Filtros
  currentFilter: FilterState = {};

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

     // Cargar agregadas con filtros
     this.metricas
       .obtenerAgregadas(
         this.currentFilter.tipo,
         this.currentFilter.prioridad
       )
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

  /**
   * Maneja cambios en los filtros
   */
  onFiltrosChange(filtros: FilterState): void {
    this.currentFilter = filtros;
    this.metricas.clearCacheKey(
      `agregadas:${filtros.tipo || 'all'}:${filtros.prioridad || 'all'}`
    );
    this.cargarDatos();
  }
}
