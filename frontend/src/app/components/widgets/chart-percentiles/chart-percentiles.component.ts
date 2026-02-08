import {
  Component,
  Input,
  ChangeDetectionStrategy,
  OnChanges,
  SimpleChanges
} from '@angular/core';
import { ChartConfiguration, ChartData } from 'chart.js';

import { MetricaAgregada } from '../../../models';
import { CardComponent } from '../shared/card.component';
import { ChartWrapperComponent } from '../chart-wrapper/chart-wrapper.component';
import {
  getBarChartOptions,
  createBarDataset
} from '../../../utils/chart-config';
import {
  obtenerLabelTipo,
  formatearTiempo
} from '../../../utils/format.utils';
import { CHART_COLORS } from '../../../types/chart.types';

/**
 * Gráfico de barras estilo box plot que visualiza percentiles
 * Muestra p50, p95 y p99 para cada tipo de incidencia
 * Permite identificar distribuciones anómalas y valores atípicos
 */
@Component({
  selector: 'app-chart-percentiles',
  standalone: true,
  imports: [CardComponent, ChartWrapperComponent],
  template: `
    <app-card
      title="Análisis de Percentiles"
      subtitle="Distribución de tiempos: P50, P95, P99 (en segundos)"
      [loading]="!data"
      [wide]="true"
    >
      <app-chart-wrapper
        chartType="bar"
        [chartData]="chartData"
        [chartOptions]="chartOptions"
      ></app-chart-wrapper>
    </app-card>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ChartPercentilesComponent implements OnChanges {
  @Input() data: MetricaAgregada[] | null = null;

  chartData: ChartData | null = null;
  chartOptions: ChartConfiguration['options'] = getBarChartOptions(true);

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['data']) {
      this.actualizarGrafica();
    }
  }

  private actualizarGrafica(): void {
    if (!this.data || this.data.length === 0) {
      this.chartData = null;
      return;
    }

    const labels = this.data.map((item) => obtenerLabelTipo(item.tipoIncidencia));

    // Preparar datos para las 3 series de percentiles
    const p50 = this.data.map((item) => item.p50Seg ?? 0);
    const p95 = this.data.map((item) => item.p95Seg ?? 0);
    const p99 = this.data.map((item) => item.p99Seg ?? 0);

    this.chartData = {
      labels,
      datasets: [
        createBarDataset(
          'P50 (Mediana)',
          p50,
          '#3ca06a',
          'rgba(60, 160, 106, 0.7)'
        ),
        createBarDataset(
          'P95 (95%)',
          p95,
          CHART_COLORS.warning,
          'rgba(240, 180, 41, 0.7)'
        ),
        createBarDataset(
          'P99 (99%)',
          p99,
          CHART_COLORS.danger,
          'rgba(214, 69, 61, 0.7)'
        )
      ]
    };

    // Configurar opciones con escala y legend
    this.chartOptions = {
      ...getBarChartOptions(true),
      scales: {
        y: {
          beginAtZero: true,
          ticks: {
            callback: (value) => {
              const segundos = value as number;
              return formatearTiempo(segundos);
            }
          }
        }
      }
    };
  }
}
