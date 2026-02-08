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
import { getScatterChartOptions, getChartColors } from '../../../utils/chart-config';
import { obtenerLabelTipo } from '../../../utils/format.utils';

/**
 * Gráfico scatter/bubble que correlaciona cantidad con tiempo de resolución
 * Cada burbuja representa un tipo de incidencia
 * X: cantidad total de incidencias
 * Y: tiempo promedio de resolución (segundos)
 * Tamaño: proporcional a la cantidad
 */
@Component({
  selector: 'app-chart-bubble',
  standalone: true,
  imports: [CardComponent, ChartWrapperComponent],
  template: `
    <app-card
      title="Análisis de Cantidad vs Tiempo de Resolución"
      subtitle="Cada burbuja representa un tipo de incidencia"
      [loading]="!data"
      [wide]="true"
    >
      <app-chart-wrapper
        chartType="bubble"
        [chartData]="chartData"
        [chartOptions]="chartOptions"
      ></app-chart-wrapper>
    </app-card>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ChartBubbleComponent implements OnChanges {
  @Input() data: MetricaAgregada[] | null = null;

  chartData: ChartData | null = null;
  chartOptions: ChartConfiguration['options'] = getScatterChartOptions();

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

    const colors = getChartColors(this.data.length);
    const datasets = this.data.map((item, index) => {
      // Normalizar el tamaño de la burbuja (15-50 pixels)
      const maxCantidad = Math.max(...this.data!.map((d) => d.cantidadTotal));
      const minCantidad = Math.min(...this.data!.map((d) => d.cantidadTotal));
      const range = maxCantidad - minCantidad || 1;
      const normalizedSize = 15 + ((item.cantidadTotal - minCantidad) / range) * 35;

      return {
        label: obtenerLabelTipo(item.tipoIncidencia),
        data: [
          {
            x: item.cantidadTotal,
            y: item.tiempoPromedioResolucionSeg ?? 0,
            r: normalizedSize
          }
        ],
        backgroundColor: colors[index] + '80', // Con transparencia
        borderColor: colors[index],
        borderWidth: 2
      };
    });

    this.chartData = {
      datasets
    };

    // Configurar opciones con labels significativos
    this.chartOptions = {
      ...getScatterChartOptions(),
      plugins: {
        legend: {
          display: true,
          position: 'bottom'
        },
        tooltip: {
          callbacks: {
            label: function(context: any) {
              const dataPoint = context.raw;
              return `Cantidad: ${dataPoint.x}, Tiempo: ${dataPoint.y}s`;
            }
          }
        }
      },
      scales: {
        x: {
          type: 'linear',
          position: 'bottom',
          title: {
            display: true,
            text: 'Cantidad Total de Incidencias'
          },
          ticks: {
            color: '#0d1f14'
          }
        },
        y: {
          title: {
            display: true,
            text: 'Tiempo Promedio de Resolución (segundos)'
          },
          ticks: {
            color: '#0d1f14'
          }
        }
      }
    };
  }
}
