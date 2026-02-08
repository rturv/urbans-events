import { Component, Input, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ErrorState } from '../../types/metricas.types';

/**
 * Componente para mostrar errores en un banner en la parte superior
 * Se muestra cuando hay errores en la carga de datos
 */
@Component({
  selector: 'app-error-banner',
  standalone: true,
  imports: [CommonModule],
  template: `
    @if (error?.hasError) {
      <div class="error-banner" role="alert">
        <div class="error-banner__content">
          <div class="error-banner__icon">⚠️</div>
          <div class="error-banner__message">
            <p class="error-banner__title">Error al conectar con métricas</p>
            <p class="error-banner__detail">{{ error?.message }}</p>
          </div>
          <button class="error-banner__close" (click)="onClose()">✕</button>
        </div>
      </div>
    }
  `,
  styles: [`
    .error-banner {
      background-color: var(--danger-500);
      color: white;
      padding: 1rem;
      border-bottom: 1px solid rgba(0, 0, 0, 0.2);
      animation: slideDown 0.3s ease-out;
    }

    .error-banner__content {
      display: flex;
      align-items: center;
      gap: 1rem;
      max-width: 100%;
    }

    .error-banner__icon {
      font-size: 1.5rem;
      flex-shrink: 0;
    }

    .error-banner__message {
      flex: 1;
      margin: 0;
    }

    .error-banner__title {
      margin: 0;
      font-weight: 600;
      font-size: 0.95rem;
    }

    .error-banner__detail {
      margin: 0.25rem 0 0;
      font-size: 0.85rem;
      opacity: 0.95;
    }

    .error-banner__close {
      background: none;
      border: none;
      color: white;
      cursor: pointer;
      font-size: 1.25rem;
      padding: 0.25rem;
      flex-shrink: 0;
      opacity: 0.8;
      transition: opacity 0.2s;
    }

    .error-banner__close:hover {
      opacity: 1;
    }

    @keyframes slideDown {
      from {
        transform: translateY(-100%);
        opacity: 0;
      }
      to {
        transform: translateY(0);
        opacity: 1;
      }
    }

    @media (max-width: 768px) {
      .error-banner {
        padding: 0.75rem;
      }

      .error-banner__content {
        gap: 0.75rem;
      }

      .error-banner__message {
        font-size: 0.9rem;
      }

      .error-banner__title {
        font-size: 0.85rem;
      }

      .error-banner__detail {
        font-size: 0.75rem;
      }
    }
  `],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ErrorBannerComponent {
  @Input() error: ErrorState | null = null;

  onClose(): void {
    if (this.error) {
      this.error.hasError = false;
    }
  }
}
