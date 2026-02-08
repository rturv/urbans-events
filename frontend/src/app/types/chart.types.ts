/**
 * Tipos para configuración de gráficos
 */

import { ChartConfiguration, ChartData } from 'chart.js';

export interface ChartConfig {
  type: 'bar' | 'doughnut' | 'line' | 'scatter' | 'bubble';
  data: ChartData;
  options: ChartConfiguration['options'];
  title: string;
  description?: string;
}

export interface BarChartConfig extends ChartConfig {
  type: 'bar';
}

export interface DoughnutChartConfig extends ChartConfig {
  type: 'doughnut';
}

export interface LineChartConfig extends ChartConfig {
  type: 'line';
}

export interface ScatterChartConfig extends ChartConfig {
  type: 'scatter';
}

export interface BubbleChartConfig extends ChartConfig {
  type: 'bubble';
}

/**
 * Colores predefinidos usando variables CSS
 */
export const CHART_COLORS = {
  primary: '#0e6f3b',      // --brand-700
  secondary: '#2b9155',    // --brand-500
  accent: '#f0b429',       // --accent-400
  danger: '#d6453d',       // --danger-500
  success: '#0a3f24',      // --brand-900
  warning: '#f0b429',      // --accent-400
  info: '#0e6f3b',         // --brand-700
  light: '#f7f5ef'         // --surface-0
};

/**
 * Paleta de colores para gráficos con múltiples series
 */
export const CHART_COLOR_PALETTE = [
  '#0a3f24',  // brand-900
  '#0e6f3b',  // brand-700
  '#2b9155',  // brand-500
  '#f0b429',  // accent-400
  '#d6453d',  // danger-500
  '#3ca06a',  // custom green
  '#4db182',  // custom green
  '#69c09a'   // custom green
];
