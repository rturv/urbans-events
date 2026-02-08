import {
  Component,
  Input,
  ChangeDetectionStrategy
} from '@angular/core';
import { CommonModule } from '@angular/common';

import { IncidenciaPendiente } from '../../../models';
import { CardComponent } from '../shared/card.component';
import {
  obtenerLabelTipo,
  obtenerLabelPrioridad,
  obtenerLabelEstado,
  formatearMinutos
} from '../../../utils/format.utils';

/**
 * Tabla responsiva que muestra las incidencias pendientes activas
 * Contiene: ID, Tipo, Prioridad, Estado, Tiempo Transcurrido
 */
@Component({
  selector: 'app-tabla-pendientes',
  standalone: true,
  imports: [CommonModule, CardComponent],
  template: `
    <app-card
      title="Incidencias Pendientes"
      subtitle="Listado de incidencias activas en espera de resolución"
      [loading]="!data"
      [wide]="true"
    >
      @if (data && data.length > 0) {
        <div class="tabla-wrapper">
          <table class="tabla-pendientes">
            <thead class="tabla-pendientes__head">
              <tr>
                <th class="tabla-pendientes__cell tabla-pendientes__cell--id">ID</th>
                <th class="tabla-pendientes__cell tabla-pendientes__cell--tipo">Tipo</th>
                <th class="tabla-pendientes__cell tabla-pendientes__cell--prioridad">Prioridad</th>
                <th class="tabla-pendientes__cell tabla-pendientes__cell--estado">Estado</th>
                <th class="tabla-pendientes__cell tabla-pendientes__cell--tiempo">Tiempo Transcurrido</th>
              </tr>
            </thead>
            <tbody class="tabla-pendientes__body">
              @for (incidencia of data; track incidencia.incidenciaId) {
                <tr class="tabla-pendientes__row" [class.tabla-pendientes__row--urgent]="isUrgent(incidencia)">
                  <td class="tabla-pendientes__cell tabla-pendientes__cell--id">
                    <span class="tabla-pendientes__id">#{{ incidencia.incidenciaId }}</span>
                  </td>
                  <td class="tabla-pendientes__cell tabla-pendientes__cell--tipo">
                    <span class="tabla-pendientes__badge tabla-pendientes__badge--tipo">
                      {{ obtenerLabelTipo(incidencia.tipo) }}
                    </span>
                  </td>
                  <td class="tabla-pendientes__cell tabla-pendientes__cell--prioridad">
                    <span class="tabla-pendientes__badge" [ngClass]="getPrioridadClass(incidencia.prioridad)">
                      {{ obtenerLabelPrioridad(incidencia.prioridad || '') }}
                    </span>
                  </td>
                  <td class="tabla-pendientes__cell tabla-pendientes__cell--estado">
                    <span class="tabla-pendientes__estado" [ngClass]="getEstadoClass(incidencia.estado)">
                      {{ obtenerLabelEstado(incidencia.estado) }}
                    </span>
                  </td>
                  <td class="tabla-pendientes__cell tabla-pendientes__cell--tiempo">
                    <span class="tabla-pendientes__tiempo" [class.tabla-pendientes__tiempo--alert]="isExceeded(incidencia)">
                      {{ formatearMinutos(incidencia.tiempoDesdeCreacionMin) }}
                    </span>
                  </td>
                </tr>
              }
            </tbody>
          </table>
        </div>
        <div class="tabla-pendientes__footer">
          <small>Total de incidencias pendientes: {{ data.length }}</small>
        </div>
      } @else if (data?.length === 0) {
        <div class="tabla-pendientes__empty">
          <p>No hay incidencias pendientes</p>
        </div>
      }
    </app-card>
  `,
  styles: [`
    .tabla-wrapper {
      overflow-x: auto;
      margin: 0 -1.5rem;
    }

    .tabla-pendientes {
      width: 100%;
      border-collapse: collapse;
      font-size: 0.9rem;
    }

    .tabla-pendientes__head {
      background-color: var(--surface-2);
      border-bottom: 2px solid var(--border-2);
      position: sticky;
      top: 0;
      z-index: 5;
    }

    .tabla-pendientes__head th {
      font-weight: 600;
      text-align: left;
      color: var(--ink-700);
    }

    .tabla-pendientes__cell {
      padding: 1rem 1.5rem;
      border-bottom: 1px solid var(--border-1);
      color: var(--ink-600);
    }

    .tabla-pendientes__cell--id {
      width: 80px;
    }

    .tabla-pendientes__cell--tipo {
      width: 140px;
    }

    .tabla-pendientes__cell--prioridad {
      width: 120px;
    }

    .tabla-pendientes__cell--estado {
      width: 120px;
    }

    .tabla-pendientes__cell--tiempo {
      width: 150px;
      text-align: right;
      padding-right: 2rem;
    }

    .tabla-pendientes__row {
      transition: background-color 0.2s ease;
    }

    .tabla-pendientes__row:hover {
      background-color: var(--surface-2);
    }

    .tabla-pendientes__row--urgent {
      background-color: rgba(214, 69, 61, 0.05);
    }

    .tabla-pendientes__row--urgent:hover {
      background-color: rgba(214, 69, 61, 0.1);
    }

    .tabla-pendientes__id {
      font-weight: 600;
      color: var(--brand-700);
      font-family: 'Courier New', monospace;
      font-size: 0.85rem;
    }

    .tabla-pendientes__badge {
      display: inline-block;
      padding: 0.35rem 0.75rem;
      border-radius: 0.35rem;
      font-size: 0.8rem;
      font-weight: 500;
      white-space: nowrap;
    }

    .tabla-pendientes__badge--tipo {
      background-color: rgba(14, 111, 59, 0.15);
      color: var(--brand-700);
    }

    .tabla-pendientes__badge {
      background-color: rgba(43, 145, 85, 0.15);
      color: var(--brand-600);
    }

    /* Prioridades */
    .tabla-pendientes__badge--alta {
      background-color: rgba(214, 69, 61, 0.15);
      color: var(--danger-600);
    }

    .tabla-pendientes__badge--media {
      background-color: rgba(240, 180, 41, 0.15);
      color: var(--accent-600);
    }

    .tabla-pendientes__badge--baja {
      background-color: rgba(60, 160, 106, 0.15);
      color: var(--brand-700);
    }

    .tabla-pendientes__estado {
      display: inline-block;
      padding: 0.35rem 0.75rem;
      border-radius: 0.35rem;
      font-size: 0.8rem;
      font-weight: 500;
      white-space: nowrap;
    }

    .tabla-pendientes__estado--pendiente {
      background-color: rgba(240, 180, 41, 0.15);
      color: var(--accent-700);
    }

    .tabla-pendientes__estado--resuelto {
      background-color: rgba(10, 63, 36, 0.15);
      color: var(--brand-900);
    }

    .tabla-pendientes__estado--rechazado {
      background-color: rgba(214, 69, 61, 0.15);
      color: var(--danger-600);
    }

    .tabla-pendientes__estado--cerrado {
      background-color: rgba(14, 111, 59, 0.15);
      color: var(--brand-700);
    }

    .tabla-pendientes__tiempo {
      font-family: 'Courier New', monospace;
      font-weight: 500;
      color: var(--ink-600);
    }

    .tabla-pendientes__tiempo--alert {
      color: var(--danger-600);
      font-weight: 600;
    }

    .tabla-pendientes__empty {
      text-align: center;
      padding: 2rem 1.5rem;
      color: var(--ink-500);
    }

    .tabla-pendientes__footer {
      padding: 1rem 1.5rem;
      border-top: 1px solid var(--border-1);
      color: var(--ink-500);
      font-size: 0.85rem;
      background-color: var(--surface-0);
      text-align: right;
    }

    @media (max-width: 1024px) {
      .tabla-pendientes__cell {
        padding: 0.75rem 1rem;
      }

      .tabla-pendientes__cell--id {
        width: 60px;
      }

      .tabla-pendientes__cell--tipo {
        width: 100px;
      }

      .tabla-pendientes__cell--prioridad {
        width: 90px;
      }

      .tabla-pendientes__cell--estado {
        width: 90px;
      }

      .tabla-pendientes__cell--tiempo {
        width: 120px;
        padding-right: 1.5rem;
      }

      .tabla-pendientes__badge {
        padding: 0.25rem 0.5rem;
        font-size: 0.75rem;
      }
    }

    @media (max-width: 640px) {
      .tabla-wrapper {
        margin: 0 -1rem;
      }

      .tabla-pendientes {
        font-size: 0.8rem;
      }

      .tabla-pendientes__cell {
        padding: 0.5rem 0.75rem;
      }

      .tabla-pendientes__cell--id {
        width: 50px;
      }

      .tabla-pendientes__cell--tipo {
        width: 80px;
      }

      .tabla-pendientes__cell--prioridad {
        width: 70px;
      }

      .tabla-pendientes__cell--estado {
        width: 70px;
      }

      .tabla-pendientes__cell--tiempo {
        width: 100px;
        padding-right: 0.75rem;
      }

      .tabla-pendientes__badge {
        padding: 0.2rem 0.4rem;
        font-size: 0.7rem;
      }

      .tabla-pendientes__id {
        font-size: 0.75rem;
      }

      .tabla-pendientes__footer {
        padding: 0.75rem 1rem;
        font-size: 0.75rem;
      }
    }
  `],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TablaPendientesComponent {
  @Input() data: IncidenciaPendiente[] | null = null;

  // Métodos de formato disponibles en el template
  obtenerLabelTipo = obtenerLabelTipo;
  obtenerLabelPrioridad = obtenerLabelPrioridad;
  obtenerLabelEstado = obtenerLabelEstado;
  formatearMinutos = formatearMinutos;

  /**
   * Determina si una incidencia es urgente (tiempo > 4 horas = 240 minutos)
   */
  isUrgent(incidencia: IncidenciaPendiente): boolean {
    return (incidencia.tiempoDesdeCreacionMin ?? 0) > 240;
  }

  /**
   * Determina si el tiempo ha excedido el límite (> 8 horas = 480 minutos)
   */
  isExceeded(incidencia: IncidenciaPendiente): boolean {
    return (incidencia.tiempoDesdeCreacionMin ?? 0) > 480;
  }

  /**
   * Retorna la clase CSS para la prioridad
   */
  getPrioridadClass(prioridad?: string): string {
    const p = prioridad?.toUpperCase() || '';
    switch (p) {
      case 'ALTA':
        return 'tabla-pendientes__badge--alta';
      case 'MEDIA':
        return 'tabla-pendientes__badge--media';
      case 'BAJA':
        return 'tabla-pendientes__badge--baja';
      default:
        return '';
    }
  }

  /**
   * Retorna la clase CSS para el estado
   */
  getEstadoClass(estado: string): string {
    const e = estado?.toUpperCase() || '';
    switch (e) {
      case 'PENDIENTE':
        return 'tabla-pendientes__estado--pendiente';
      case 'RESUELTO':
        return 'tabla-pendientes__estado--resuelto';
      case 'RECHAZADO':
        return 'tabla-pendientes__estado--rechazado';
      case 'CERRADO':
        return 'tabla-pendientes__estado--cerrado';
      default:
        return '';
    }
  }
}
