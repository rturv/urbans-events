import {
  Component,
  Input,
  ChangeDetectionStrategy,
  OnChanges,
  SimpleChanges
} from '@angular/core';
import { ChartConfiguration, ChartData } from 'chart.js';

import { EstadisticasTipo } from '../../../models';
import { CardComponent } from '../shared/card.component';
import { ChartWrapperComponent } from '../chart-wrapper/chart-wrapper.component';
import { getBarChartOptions, getChartColors } from '../../../utils/chart-config';
import { obtenerLabelTipo } from '../../../utils/format.utils';

/**
 * Gráfico de barras horizontal que muestra incidencias por tipo
 */
@Component({
  selector: 'app-chart-tipos',
  standalone: true,
  imports: [CardComponent, ChartWrapperComponent],
  template: `
    <app-card
      title="Incidencias por Tipo"
      subtitle="Conteo agregado por tipo"
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
export class ChartTiposComponent implements OnChanges {
  @Input() data: EstadisticasTipo[] | null = null;

  chartData: ChartData | null = null;
  chartOptions: ChartConfiguration['options'] = getBarChartOptions();

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

    const labels = this.data.map((item) => obtenerLabelTipo(item.tipo));
    const valores = this.data.map((item) => item.cantidad);
    const colors = getChartColors(this.data.length);

    this.chartData = {
      labels,
      datasets: [
        {
          label: 'Cantidad',
          data: valores,
          backgroundColor: colors,
          borderColor: colors.map((c) => c + 'dd'),
          borderWidth: 1,
          borderRadius: 4
        }
      ]
    };

    // Opciones para bar horizontal
    this.chartOptions = {
      ...getBarChartOptions(),
      indexAxis: 'y',
      scales: {
        x: {
          beginAtZero: true,
          ticks: {
            color: '#0d1f14'
          }
        }
      }
    };
  }
}
