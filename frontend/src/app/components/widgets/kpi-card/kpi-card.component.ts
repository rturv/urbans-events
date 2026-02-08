import {
  Component,
  Input,
  ChangeDetectionStrategy,
  OnChanges,
  SimpleChanges
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { ResumenMetricas } from '../../../models';
import { CardComponent } from '../shared/card.component';
import {
  formatearNumero,
  formatearPorcentaje,
  formatearTiempo
} from '../../../utils/format.utils';

/**
 * Componente que muestra 7 tarjetas KPI principales
 * Contiene métricas clave del resumen de incidencias
 */
@Component({
  selector: 'app-kpi-card',
  standalone: true,
  imports: [CommonModule, CardComponent],
  template: `
    <div class="kpi-grid">
      @if (data) {
        <!-- KPI: Total de Incidencias -->
        <app-card [wide]="true">
          <div class="kpi-item">
            <div class="kpi-item__header">
              <h3 class="kpi-item__label">Total de Incidencias</h3>
            </div>
            <div class="kpi-item__value">
              {{ formatearNumero(data.totalIncidencias) }}
            </div>
            <div class="kpi-item__description">
              Incidencias registradas
            </div>
          </div>
        </app-card>

        <!-- KPI: Resueltas -->
        <app-card>
          <div class="kpi-item">
            <div class="kpi-item__header">
              <h3 class="kpi-item__label">Resueltas</h3>
              <span class="kpi-item__badge kpi-item__badge--success">✓</span>
            </div>
            <div class="kpi-item__value kpi-item__value--success">
              {{ formatearNumero(data.incidenciasResueltas) }}
            </div>
            <div class="kpi-item__description">
              {{ formatearPorcentaje(data.tasaExitoPct) }} éxito
            </div>
          </div>
        </app-card>

        <!-- KPI: Pendientes -->
        <app-card>
          <div class="kpi-item">
            <div class="kpi-item__header">
              <h3 class="kpi-item__label">Pendientes</h3>
              <span class="kpi-item__badge kpi-item__badge--warning">⏱</span>
            </div>
            <div class="kpi-item__value kpi-item__value--warning">
              {{ formatearNumero(data.incidenciasPendientes) }}
            </div>
            <div class="kpi-item__description">
              {{ formatearPorcentaje(data.tasaPendientePct) }} pendiente
            </div>
          </div>
        </app-card>

        <!-- KPI: Rechazadas -->
        <app-card>
          <div class="kpi-item">
            <div class="kpi-item__header">
              <h3 class="kpi-item__label">Rechazadas</h3>
              <span class="kpi-item__badge kpi-item__badge--danger">✗</span>
            </div>
            <div class="kpi-item__value kpi-item__value--danger">
              {{ formatearNumero(data.incidenciasRechazadas) }}
            </div>
            <div class="kpi-item__description">
              {{ formatearPorcentaje(data.tasaFracasoPct) }} fracaso
            </div>
          </div>
        </app-card>

        <!-- KPI: Tasa de Éxito -->
        <app-card>
          <div class="kpi-item">
            <div class="kpi-item__header">
              <h3 class="kpi-item__label">Tasa de Éxito</h3>
            </div>
            <div class="kpi-item__value kpi-item__value--accent">
              {{ formatearPorcentaje(data.tasaExitoPct) }}
            </div>
            <div class="kpi-item__description">
              Incidencias resueltas
            </div>
          </div>
        </app-card>

        <!-- KPI: Tasa de Fracaso -->
        <app-card>
          <div class="kpi-item">
            <div class="kpi-item__header">
              <h3 class="kpi-item__label">Tasa de Fracaso</h3>
            </div>
            <div class="kpi-item__value kpi-item__value--alert">
              {{ formatearPorcentaje(data.tasaFracasoPct) }}
            </div>
            <div class="kpi-item__description">
              Incidencias rechazadas
            </div>
          </div>
        </app-card>

        <!-- KPI: Tiempo Promedio -->
        <app-card>
          <div class="kpi-item">
            <div class="kpi-item__header">
              <h3 class="kpi-item__label">Tiempo Promedio</h3>
            </div>
            <div class="kpi-item__value kpi-item__value--info">
              {{ formatearTiempo(data.tiempoPromedioResolucionSeg) }}
            </div>
            <div class="kpi-item__description">
              De resolución
            </div>
          </div>
        </app-card>
      } @else {
        <div class="kpi-empty">
          <p>Cargando métricas...</p>
        </div>
      }
    </div>
  `,
  styles: [`
    .kpi-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 1rem;
    }

    .kpi-item {
      padding: 1rem 0;
    }

    .kpi-item__header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 0.75rem;
    }

    .kpi-item__label {
      margin: 0;
      font-size: 0.875rem;
      font-weight: 600;
      color: var(--ink-700);
      text-transform: uppercase;
      letter-spacing: 0.02em;
    }

    .kpi-item__badge {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      width: 1.5rem;
      height: 1.5rem;
      border-radius: 50%;
      font-size: 0.75rem;
      font-weight: 600;
    }

    .kpi-item__badge--success {
      background-color: rgba(10, 63, 36, 0.15);
      color: var(--brand-900);
    }

    .kpi-item__badge--warning {
      background-color: rgba(240, 180, 41, 0.15);
      color: var(--accent-400);
    }

    .kpi-item__badge--danger {
      background-color: rgba(214, 69, 61, 0.15);
      color: var(--danger-500);
    }

    .kpi-item__value {
      font-size: 1.75rem;
      font-weight: 700;
      color: var(--ink-900);
      margin-bottom: 0.5rem;
      line-height: 1.2;
    }

    .kpi-item__value--success {
      color: var(--brand-900);
    }

    .kpi-item__value--warning {
      color: var(--accent-400);
    }

    .kpi-item__value--danger {
      color: var(--danger-500);
    }

    .kpi-item__value--accent {
      color: var(--brand-700);
    }

    .kpi-item__value--alert {
      color: var(--danger-600);
    }

    .kpi-item__value--info {
      color: var(--brand-600);
    }

    .kpi-item__description {
      font-size: 0.8rem;
      color: var(--ink-500);
      margin: 0;
    }

    .kpi-empty {
      grid-column: 1 / -1;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 2rem;
      color: var(--ink-500);
    }

    @media (max-width: 1024px) {
      .kpi-grid {
        grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
        gap: 0.75rem;
      }

      .kpi-item__value {
        font-size: 1.5rem;
      }

      .kpi-item__label {
        font-size: 0.75rem;
      }
    }

    @media (max-width: 640px) {
      .kpi-grid {
        grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
        gap: 0.5rem;
      }

      .kpi-item {
        padding: 0.75rem 0;
      }

      .kpi-item__value {
        font-size: 1.25rem;
      }

      .kpi-item__label {
        font-size: 0.7rem;
      }

      .kpi-item__description {
        font-size: 0.7rem;
      }
    }
  `],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class KpiCardComponent implements OnChanges {
  @Input() data: ResumenMetricas | null = null;

  // Métodos de formato disponibles en el template
  formatearNumero = formatearNumero;
  formatearPorcentaje = formatearPorcentaje;
  formatearTiempo = formatearTiempo;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['data']) {
      // Component actualiza automáticamente via change detection
    }
  }
}
