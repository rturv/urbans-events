import { Component, Input, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChartConfiguration, ChartData } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';

/**
 * Componente base reutilizable para todos los gráficos
 * Maneja la presentación de datos y opciones comunes
 */
@Component({
  selector: 'app-chart-wrapper',
  standalone: true,
  imports: [CommonModule, BaseChartDirective],
  template: `
    @if (chartData && chartOptions) {
      <canvas
        baseChart
        [type]="chartType"
        [data]="chartData"
        [options]="chartOptions"
        class="chart-wrapper__canvas"
      ></canvas>
    } @else {
      <div class="chart-wrapper__empty">
        <p>No hay datos disponibles</p>
      </div>
    }
  `,
  styles: [`
    .chart-wrapper__canvas {
      display: block;
      width: 100%;
      height: 100%;
      min-height: 300px;
    }

    .chart-wrapper__empty {
      display: flex;
      align-items: center;
      justify-content: center;
      height: 300px;
      color: var(--ink-500);
      font-size: 0.95rem;
    }
  `],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ChartWrapperComponent {
  @Input() chartType: 'bar' | 'doughnut' | 'line' | 'scatter' | 'bubble' = 'bar';
  @Input() chartData: ChartData | null = null;
  @Input() chartOptions: ChartConfiguration['options'] | null = null;
}
