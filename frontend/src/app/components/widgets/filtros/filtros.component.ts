import {
  Component,
  Output,
  EventEmitter,
  ChangeDetectionStrategy,
  OnInit
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormGroup, FormBuilder } from '@angular/forms';

import { CardComponent } from '../shared/card.component';
import { FilterState, TipoIncidencia, Prioridad } from '../../../types/metricas.types';

/**
 * Componente de filtros con formulario reactivo
 * Permite filtrar por tipo de incidencia y prioridad
 * Emite eventos cuando los filtros cambian
 */
@Component({
  selector: 'app-filtros',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, CardComponent],
  template: `
    <app-card
      title="Filtros"
      subtitle="Ajusta los parámetros para filtrar incidencias"
    >
      <form [formGroup]="filterForm" class="filtros-form">
        <div class="filtros-grid">
          <!-- Dropdown: Tipo de Incidencia -->
          <div class="filtros-group">
            <label for="tipoSelect" class="filtros-label">
              Tipo de Incidencia
            </label>
            <select
              id="tipoSelect"
              formControlName="tipo"
              class="filtros-select"
            >
              <option value="">-- Todos los tipos --</option>
              @for (tipo of tipos; track tipo) {
                <option [value]="tipo">{{ obtenerLabelTipo(tipo) }}</option>
              }
            </select>
          </div>

          <!-- Dropdown: Prioridad -->
          <div class="filtros-group">
            <label for="prioridadSelect" class="filtros-label">
              Prioridad
            </label>
            <select
              id="prioridadSelect"
              formControlName="prioridad"
              class="filtros-select"
            >
              <option value="">-- Todas las prioridades --</option>
              @for (prioridad of prioridades; track prioridad) {
                <option [value]="prioridad">{{ obtenerLabelPrioridad(prioridad) }}</option>
              }
            </select>
          </div>
        </div>

        <!-- Botones de acción -->
        <div class="filtros-actions">
          <button
            type="button"
            class="filtros-btn filtros-btn--primary"
            (click)="aplicarFiltros()"
          >
            Aplicar Filtros
          </button>
          <button
            type="button"
            class="filtros-btn filtros-btn--secondary"
            (click)="limpiarFiltros()"
          >
            Limpiar
          </button>
        </div>
      </form>
    </app-card>
  `,
  styles: [`
    .filtros-form {
      display: flex;
      flex-direction: column;
      gap: 1.5rem;
    }

    .filtros-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 1.5rem;
    }

    .filtros-group {
      display: flex;
      flex-direction: column;
      gap: 0.5rem;
    }

    .filtros-label {
      font-size: 0.9rem;
      font-weight: 600;
      color: var(--ink-700);
      text-transform: uppercase;
      letter-spacing: 0.02em;
    }

    .filtros-select {
      padding: 0.75rem 1rem;
      border: 1px solid var(--border-1);
      border-radius: 0.5rem;
      background-color: var(--surface-0);
      color: var(--ink-900);
      font-size: 0.95rem;
      font-family: inherit;
      transition: all 0.2s ease;
      cursor: pointer;
    }

    .filtros-select:hover {
      border-color: var(--border-2);
      background-color: var(--surface-1);
    }

    .filtros-select:focus {
      outline: none;
      border-color: var(--brand-700);
      box-shadow: 0 0 0 3px rgba(14, 111, 59, 0.1);
    }

    .filtros-actions {
      display: flex;
      gap: 1rem;
      justify-content: flex-start;
      flex-wrap: wrap;
    }

    .filtros-btn {
      padding: 0.75rem 1.5rem;
      border: none;
      border-radius: 0.5rem;
      font-size: 0.95rem;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.2s ease;
      text-transform: uppercase;
      letter-spacing: 0.02em;
    }

    .filtros-btn--primary {
      background-color: var(--brand-700);
      color: white;
    }

    .filtros-btn--primary:hover {
      background-color: var(--brand-800);
      box-shadow: 0 4px 12px rgba(14, 111, 59, 0.3);
    }

    .filtros-btn--primary:active {
      background-color: var(--brand-900);
      transform: scale(0.98);
    }

    .filtros-btn--secondary {
      background-color: var(--surface-2);
      color: var(--ink-700);
      border: 1px solid var(--border-1);
    }

    .filtros-btn--secondary:hover {
      background-color: var(--surface-3);
      border-color: var(--border-2);
    }

    .filtros-btn--secondary:active {
      background-color: var(--surface-4);
      transform: scale(0.98);
    }

    @media (max-width: 768px) {
      .filtros-grid {
        grid-template-columns: 1fr;
      }

      .filtros-actions {
        justify-content: stretch;
      }

      .filtros-btn {
        flex: 1;
        min-width: 100px;
      }
    }
  `],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FiltrosComponent implements OnInit {
  @Output() filtrosChange = new EventEmitter<FilterState>();

  filterForm!: FormGroup;

  // Opciones disponibles para los dropdowns
  tipos = Object.values(TipoIncidencia);
  prioridades = Object.values(Prioridad);

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.inicializarFormulario();
  }

  /**
   * Inicializa el formulario reactivo
   */
  private inicializarFormulario(): void {
    this.filterForm = this.fb.group({
      tipo: [''],
      prioridad: ['']
    });
  }

  /**
   * Aplica los filtros actuales emitiendo el evento
   */
  aplicarFiltros(): void {
    const filterState: FilterState = {
      tipo: this.filterForm.get('tipo')?.value || undefined,
      prioridad: this.filterForm.get('prioridad')?.value || undefined
    };

    // Limpiar valores vacíos
    if (!filterState.tipo) delete filterState.tipo;
    if (!filterState.prioridad) delete filterState.prioridad;

    this.filtrosChange.emit(filterState);
  }

  /**
   * Limpia todos los filtros
   */
  limpiarFiltros(): void {
    this.filterForm.reset({
      tipo: '',
      prioridad: ''
    });

    this.filtrosChange.emit({});
  }

  /**
   * Obtiene el label legible para un tipo de incidencia
   */
  obtenerLabelTipo(tipo: string): string {
    const labels: Record<string, string> = {
      [TipoIncidencia.BASURA]: 'Basura',
      [TipoIncidencia.OBRAS]: 'Obras',
      [TipoIncidencia.ALUMBRADO]: 'Alumbrado',
      [TipoIncidencia.SEGURIDAD]: 'Seguridad',
      [TipoIncidencia.SANIDAD]: 'Sanidad'
    };
    return labels[tipo] || tipo;
  }

  /**
   * Obtiene el label legible para una prioridad
   */
  obtenerLabelPrioridad(prioridad: string): string {
    const labels: Record<string, string> = {
      [Prioridad.ALTA]: 'Alta',
      [Prioridad.MEDIA]: 'Media',
      [Prioridad.BAJA]: 'Baja'
    };
    return labels[prioridad] || prioridad;
  }
}
