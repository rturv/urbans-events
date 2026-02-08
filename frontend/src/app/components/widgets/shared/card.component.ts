import { Component, Input, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';

/**
 * Componente card reutilizable para envolver contenido
 * Proporciona estilos consistentes para todos los widgets
 */
@Component({
  selector: 'app-card',
  standalone: true,
  imports: [CommonModule],
  template: `
    <article class="card" [class.card--wide]="wide">
      @if (title) {
        <div class="card__header">
          <h2 class="card__title">{{ title }}</h2>
          @if (subtitle) {
            <span class="card__subtitle">{{ subtitle }}</span>
          }
        </div>
      }
      <div class="card__content">
        <ng-content></ng-content>
      </div>
      @if (loading) {
        <div class="card__loading">
          <span class="card__spinner"></span>
          <p>Cargando datos...</p>
        </div>
      }
      @if (error) {
        <div class="card__error">
          <p>{{ error }}</p>
        </div>
      }
    </article>
  `,
  styles: [`
    .card {
      background: var(--surface-1);
      border: 1px solid var(--border-1);
      border-radius: 0.75rem;
      padding: 1.5rem;
      box-shadow: 0 2px 8px rgba(10, 63, 36, 0.08);
      transition: all 0.3s ease;
      position: relative;
    }

    .card:hover {
      box-shadow: 0 4px 16px rgba(10, 63, 36, 0.12);
      border-color: var(--brand-700);
    }

    .card--wide {
      grid-column: span 2;
    }

    .card__header {
      margin-bottom: 1.5rem;
      border-bottom: 1px solid var(--border-1);
      padding-bottom: 1rem;
    }

    .card__title {
      margin: 0;
      font-size: 1.125rem;
      font-weight: 600;
      color: var(--ink-900);
    }

    .card__subtitle {
      display: block;
      margin-top: 0.25rem;
      font-size: 0.875rem;
      color: var(--ink-500);
    }

    .card__content {
      position: relative;
    }

    .card__loading {
      position: absolute;
      inset: 0;
      display: flex;
      align-items: center;
      justify-content: center;
      flex-direction: column;
      background: rgba(255, 255, 255, 0.95);
      border-radius: 0.5rem;
      gap: 1rem;
      z-index: 10;
    }

    .card__spinner {
      width: 2rem;
      height: 2rem;
      border: 3px solid var(--border-1);
      border-top-color: var(--brand-700);
      border-radius: 50%;
      animation: spin 1s linear infinite;
    }

    .card__loading p {
      margin: 0;
      font-size: 0.9rem;
      color: var(--ink-500);
    }

    .card__error {
      padding: 1rem;
      background-color: rgba(214, 69, 61, 0.1);
      border: 1px solid var(--danger-500);
      border-radius: 0.5rem;
      color: var(--danger-500);
    }

    .card__error p {
      margin: 0;
      font-size: 0.9rem;
    }

    @keyframes spin {
      to {
        transform: rotate(360deg);
      }
    }

    @media (max-width: 1024px) {
      .card {
        padding: 1.25rem;
      }

      .card--wide {
        grid-column: span 1;
      }
    }

    @media (max-width: 640px) {
      .card {
        padding: 1rem;
      }

      .card__header {
        margin-bottom: 1rem;
        padding-bottom: 0.75rem;
      }

      .card__title {
        font-size: 1rem;
      }

      .card__subtitle {
        font-size: 0.8rem;
      }
    }
  `],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CardComponent {
  @Input() title: string | null = null;
  @Input() subtitle: string | null = null;
  @Input() wide = false;
  @Input() loading = false;
  @Input() error: string | null = null;
}
