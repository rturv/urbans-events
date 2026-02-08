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
import { obtenerLabelTipo } from '../../../utils/format.utils';
import { CHART_COLORS } from '../../../types/chart.types';

/**
 * Gráfico de barras agrupado que muestra tasas de éxito, fracaso y pendiente
 * Compara el desempeño entre diferentes tipos de incidencias
 */
@Component({
  selector: 'app-chart-tasas',
  standalone: true,
  imports: [CardComponent, ChartWrapperComponent],
  template: `
    <app-card
      title="Tasas por Tipo de Incidencia"
      subtitle="Éxito, Fracaso y Pendiente"
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
export class ChartTasasComponent implements OnChanges {
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

    // Preparar datos para las 3 series
    const tasaExito = this.data.map((item) => item.tasaExitoPct ?? 0);
    const tasaFracaso = this.data.map((item) => item.tasaFracasoPct ?? 0);
    const tasaPendiente = this.data.map((item) => item.tasaPendientePct ?? 0);

    this.chartData = {
      labels,
      datasets: [
        createBarDataset(
          'Tasa Éxito (%)',
          tasaExito,
          CHART_COLORS.success
        ),
        createBarDataset(
          'Tasa Fracaso (%)',
          tasaFracaso,
          CHART_COLORS.danger
        ),
        createBarDataset(
          'Tasa Pendiente (%)',
          tasaPendiente,
          CHART_COLORS.warning
        )
      ]
    };

    // Configurar opciones con escala y legend
    this.chartOptions = {
      ...getBarChartOptions(true),
      scales: {
        y: {
          beginAtZero: true,
          max: 100,
          ticks: {
            callback: function(value) {
              return (value as number) + '%';
            }
          }
        }
      }
    };
  }
}
