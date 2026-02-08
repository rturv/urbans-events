/**
 * Configuraciones predefinidas para Chart.js
 */

import { ChartConfiguration } from 'chart.js';
import { CHART_COLORS, CHART_COLOR_PALETTE } from '../types/chart.types';

/**
 * Opciones comunes para todos los gráficos
 */
export const commonChartOptions: ChartConfiguration['options'] = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      display: true,
      labels: {
        color: CHART_COLORS.info,
        font: {
          family: "'Source Sans 3', sans-serif",
          size: 12
        },
        padding: 15,
        usePointStyle: true
      }
    }
  }
};

/**
 * Opciones para gráficos de barras
 */
export function getBarChartOptions(showLegend = false): ChartConfiguration['options'] {
  return {
    ...commonChartOptions,
    plugins: {
      legend: {
        display: showLegend
      }
    },
    scales: {
      x: {
        grid: { display: false }
      },
      y: {
        beginAtZero: true,
        ticks: {
          color: CHART_COLORS.info,
          font: {
            size: 11
          }
        }
      }
    }
  };
}

/**
 * Opciones para gráficos doughnut/pie
 */
export function getDoughnutChartOptions(): ChartConfiguration['options'] {
  return {
    ...commonChartOptions,
    plugins: {
      legend: {
        position: 'bottom'
      }
    }
  };
}

/**
 * Opciones para gráficos de línea
 */
export function getLineChartOptions(): ChartConfiguration['options'] {
  return {
    ...commonChartOptions,
    scales: {
      x: {
        grid: { display: false }
      },
      y: {
        beginAtZero: true,
        ticks: {
          color: CHART_COLORS.info,
          font: {
            size: 11
          }
        }
      }
    }
  };
}

/**
 * Opciones para gráficos scatter/bubble
 */
export function getScatterChartOptions(): ChartConfiguration['options'] {
  return {
    ...commonChartOptions,
    scales: {
      x: {
        type: 'linear',
        position: 'bottom',
        ticks: {
          color: CHART_COLORS.info
        }
      },
      y: {
        ticks: {
          color: CHART_COLORS.info
        }
      }
    }
  };
}

/**
 * Paleta de colores para usar en gráficos
 */
export function getChartColors(count: number): string[] {
  if (count <= CHART_COLOR_PALETTE.length) {
    return CHART_COLOR_PALETTE.slice(0, count);
  }

  // Si necesitamos más colores, generar tonos
  const base = CHART_COLOR_PALETTE;
  const colors = [...base];
  for (let i = base.length; i < count; i++) {
    const hue = (i * 360) / count;
    colors.push(`hsl(${hue}, 70%, 50%)`);
  }
  return colors;
}

/**
 * Crea un dataset para gráficos de barras
 */
export function createBarDataset(
  label: string,
  data: (number | null)[],
  color?: string,
  backgroundColor?: string
) {
  return {
    label,
    data,
    borderColor: color || CHART_COLORS.primary,
    backgroundColor: backgroundColor || color || CHART_COLORS.primary,
    borderWidth: 1,
    borderRadius: 4
  };
}

/**
 * Crea un dataset para gráficos de línea
 */
export function createLineDataset(
  label: string,
  data: (number | null)[],
  color?: string,
  fill = false
) {
  return {
    label,
    data,
    borderColor: color || CHART_COLORS.primary,
    backgroundColor: fill
      ? (color || CHART_COLORS.primary) + '20'
      : 'transparent',
    borderWidth: 2,
    fill,
    tension: 0.4,
    pointRadius: 3,
    pointHoverRadius: 5,
    pointBackgroundColor: color || CHART_COLORS.primary,
    pointBorderColor: 'white',
    pointBorderWidth: 2
  };
}

/**
 * Crea un dataset para gráficos doughnut
 */
export function createDoughnutDataset(
  labels: string[],
  data: number[],
  colors?: string[]
) {
  return {
    labels,
    datasets: [
      {
        data,
        backgroundColor: colors || getChartColors(data.length),
        borderColor: '#ffffff',
        borderWidth: 2,
        hoverBorderWidth: 3
      }
    ]
  };
}
