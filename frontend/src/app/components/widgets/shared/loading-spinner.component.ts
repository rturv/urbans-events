import { Component, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';

/**
 * Componente spinner de carga reutilizable
 */
@Component({
  selector: 'app-loading-spinner',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="spinner">
      <div class="spinner__circle"></div>
      <p class="spinner__text">Cargando datos...</p>
    </div>
  `,
  styles: [`
    .spinner {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      gap: 1rem;
      padding: 2rem;
    }

    .spinner__circle {
      width: 3rem;
      height: 3rem;
      border: 4px solid var(--border-1);
      border-top-color: var(--brand-700);
      border-radius: 50%;
      animation: spin 1s linear infinite;
    }

    .spinner__text {
      margin: 0;
      color: var(--ink-500);
      font-size: 0.95rem;
    }

    @keyframes spin {
      to {
        transform: rotate(360deg);
      }
    }
  `],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class LoadingSpinnerComponent {}
