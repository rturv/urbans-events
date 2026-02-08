import { Component, Input, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';

/**
 * Componente header que muestra información del estado de la aplicación
 * y permite acciones de refresh manual
 */
@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule],
  template: `
    <header class="header">
      <div class="header__container">
        <div class="header__brand">
          <p class="header__eyebrow">Junta de Andalucía · Urban Events</p>
          <h1 class="header__title">Panel de Incidencias Urbanas</h1>
          <p class="header__subtitle">
            Visualización agregada de incidencias, prioridad y notificaciones desde eventos Kafka
          </p>
        </div>

        <div class="header__actions">
          <button class="header__button" (click)="onRefresh()">
            ⟳ Actualizar métricas
          </button>
          <div class="header__meta">
            <span class="header__label">Última actualización:</span>
            <span class="header__time">{{ lastUpdated || 'Pendiente' }}</span>
          </div>
        </div>

        <div class="header__status" [class]="'header__status--' + status">
          @if (status === 'loading') {
            <span class="header__status-icon">⏳</span>
            <span>Cargando datos...</span>
          }
          @if (status === 'ready') {
            <span class="header__status-icon">✓</span>
            <span>Datos sincronizados</span>
          }
          @if (status === 'error') {
            <span class="header__status-icon">✕</span>
            <span>Error de conexión</span>
          }
        </div>
      </div>
    </header>
  `,
  styles: [`
    .header {
      background: linear-gradient(180deg, var(--surface-1) 0%, var(--surface-0) 100%);
      border-bottom: 1px solid var(--border-1);
      padding: 2rem;
      margin-bottom: 2rem;
    }

    .header__container {
      max-width: 1400px;
      margin: 0 auto;
    }

    .header__brand {
      margin-bottom: 1.5rem;
    }

    .header__eyebrow {
      margin: 0;
      font-size: 0.875rem;
      color: var(--ink-500);
      text-transform: uppercase;
      letter-spacing: 0.05em;
      font-weight: 600;
    }

    .header__title {
      margin: 0.5rem 0 0.25rem;
      font-size: 2rem;
      color: var(--ink-900);
      font-family: 'Marcellus', serif;
      font-weight: 400;
    }

    .header__subtitle {
      margin: 0.5rem 0 0;
      font-size: 0.95rem;
      color: var(--ink-500);
    }

    .header__actions {
      display: flex;
      align-items: center;
      gap: 2rem;
      margin-bottom: 1.5rem;
      flex-wrap: wrap;
    }

    .header__button {
      background-color: var(--brand-700);
      color: white;
      border: none;
      padding: 0.75rem 1.5rem;
      border-radius: 0.5rem;
      font-size: 0.95rem;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.2s;
    }

    .header__button:hover {
      background-color: var(--brand-900);
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(14, 111, 59, 0.2);
    }

    .header__button:active {
      transform: translateY(0);
    }

    .header__meta {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      font-size: 0.9rem;
    }

    .header__label {
      color: var(--ink-500);
      font-weight: 500;
    }

    .header__time {
      color: var(--brand-700);
      font-weight: 600;
      font-family: 'Courier New', monospace;
    }

    .header__status {
      display: inline-flex;
      align-items: center;
      gap: 0.5rem;
      padding: 0.5rem 1rem;
      border-radius: 0.375rem;
      font-size: 0.9rem;
      font-weight: 500;
    }

    .header__status-icon {
      font-size: 1.2rem;
    }

    .header__status--loading {
      background-color: rgba(240, 180, 41, 0.15);
      color: var(--accent-400);
      border: 1px solid var(--accent-400);
    }

    .header__status--ready {
      background-color: rgba(10, 63, 36, 0.15);
      color: var(--brand-900);
      border: 1px solid var(--brand-900);
    }

    .header__status--error {
      background-color: rgba(214, 69, 61, 0.15);
      color: var(--danger-500);
      border: 1px solid var(--danger-500);
    }

    @media (max-width: 1024px) {
      .header {
        padding: 1.5rem;
        margin-bottom: 1.5rem;
      }

      .header__title {
        font-size: 1.5rem;
      }

      .header__actions {
        gap: 1rem;
      }
    }

    @media (max-width: 640px) {
      .header {
        padding: 1rem;
        margin-bottom: 1rem;
      }

      .header__title {
        font-size: 1.25rem;
      }

      .header__subtitle {
        font-size: 0.85rem;
      }

      .header__actions {
        flex-direction: column;
        gap: 0.75rem;
      }

      .header__button {
        width: 100%;
      }

      .header__meta {
        width: 100%;
        flex-direction: column;
        align-items: flex-start;
      }
    }
  `],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HeaderComponent {
  @Input() status: 'loading' | 'ready' | 'error' = 'loading';
  @Input() lastUpdated: string | null = null;

  onRefresh(): void {
    // Se dispara desde el dashboard
  }
}
