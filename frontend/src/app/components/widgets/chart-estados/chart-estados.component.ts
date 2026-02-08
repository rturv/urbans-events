import {
  Component,
  Input,
  ChangeDetectionStrategy,
  OnChanges,
  SimpleChanges
} from '@angular/core';
import { ChartConfiguration, ChartData } from 'chart.js';

import { ResumenMetricas } from '../../../models';
import { CardComponent } from '../shared/card.component';
import { ChartWrapperComponent } from '../chart-wrapper/chart-wrapper.component';
import {
  getDoughnutChartOptions,
  createDoughnutDataset,
  getChartColors
} from '../../../utils/chart-config';
import { CHART_COLORS } from '../../../types/chart.types';

/**
 * Gráfico Doughnut que muestra la distribución de estados
 * RESUELTO, PENDIENTE, RECHAZADO
 */
@Component({
  selector: 'app-chart-estados',
  standalone: true,
  imports: [CardComponent, ChartWrapperComponent],
  template: `
    <app-card
      title="Distribución por Estado"
      subtitle="Resuelto, Pendiente, Rechazado"
      [loading]="!data"
    >
      <app-chart-wrapper
        chartType="doughnut"
        [chartData]="chartData"
        [chartOptions]="chartOptions"
      ></app-chart-wrapper>
    </app-card>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ChartEstadosComponent implements OnChanges {
  @Input() data: ResumenMetricas | null = null;

  chartData: ChartData | null = null;
  chartOptions: ChartConfiguration['options'] = getDoughnutChartOptions();

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['data']) {
      this.actualizarGrafica();
    }
  }

  private actualizarGrafica(): void {
    if (!this.data) {
      this.chartData = null;
      return;
    }

    const labels = ['Resuelto', 'Pendiente', 'Rechazado'];
    const values = [
      this.data.incidenciasResueltas,
      this.data.incidenciasPendientes,
      this.data.incidenciasRechazadas
    ];

    const colors = [
      CHART_COLORS.success,    // Verde para resuelto
      CHART_COLORS.warning,    // Amarillo para pendiente
      CHART_COLORS.danger      // Rojo para rechazado
    ];

    this.chartData = createDoughnutDataset(labels, values, colors);
  }
}
