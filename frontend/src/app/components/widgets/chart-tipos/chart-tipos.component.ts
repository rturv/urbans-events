import {
  Component,
  Input,
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
  `
})
export class ChartTiposComponent implements OnChanges {
  @Input() data: EstadisticasTipo[] | null = null;

  chartData: ChartData | null = null;
  chartOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    indexAxis: 'y',
    plugins: {
      legend: {
        display: true,
        labels: {
          color: '#0e6f3b',
          font: { family: "'Source Sans 3', sans-serif", size: 12 },
          padding: 15,
          usePointStyle: true
        }
      }
    },
    scales: {
      x: {
        beginAtZero: true,
        ticks: { color: '#0e6f3b' }
      }
    }
  };

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['data']) {
      try {
        this.actualizarGrafica();
      } catch (error) {
        console.error('Error en ChartTiposComponent:', error);
        this.chartData = null;
      }
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
  }
}
