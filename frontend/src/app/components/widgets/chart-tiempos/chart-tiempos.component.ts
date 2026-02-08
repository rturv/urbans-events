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
 * Gráfico de barras agrupado que muestra tiempos de resolución
 * Compara tiempo mínimo, promedio y máximo entre tipos de incidencias
 */
@Component({
  selector: 'app-chart-tiempos',
  standalone: true,
  imports: [CardComponent, ChartWrapperComponent],
  template: `
    <app-card
      title="Tiempos de Resolución"
      subtitle="Mínimo, Promedio y Máximo (en segundos)"
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
export class ChartTiemposComponent implements OnChanges {
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

    // Preparar datos para las 3 series de tiempo
    const tiempoMin = this.data.map((item) => item.tiempoMinResolucionSeg ?? 0);
    const tiempoPromedio = this.data.map((item) => item.tiempoPromedioResolucionSeg ?? 0);
    const tiempoMax = this.data.map((item) => item.tiempoMaxResolucionSeg ?? 0);

    this.chartData = {
      labels,
      datasets: [
        createBarDataset(
          'Mínimo (seg)',
          tiempoMin,
          '#3ca06a',
          'rgba(60, 160, 106, 0.7)'
        ),
        createBarDataset(
          'Promedio (seg)',
          tiempoPromedio,
          CHART_COLORS.info,
          'rgba(14, 111, 59, 0.7)'
        ),
        createBarDataset(
          'Máximo (seg)',
          tiempoMax,
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
