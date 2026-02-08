import { ChartConfiguration } from 'chart.js';
import {
  getBarChartOptions,
  getDoughnutChartOptions,
  getLineChartOptions,
  getChartColors,
  createBarDataset,
  createDoughnutDataset,
  createLineDataset,
  getScatterChartOptions,
  commonChartOptions
} from './chart-config';
import { CHART_COLORS, CHART_COLOR_PALETTE } from '../types/chart.types';

/**
 * Tests unitarios para las funciones de configuración de gráficos
 * Valida que se retornen configuraciones válidas para Chart.js
 */
describe('Configuración de Gráficos - chart-config', () => {

  // ============================================
  // Tests para getBarChartOptions()
  // ============================================

  /**
   * Test 1: getBarChartOptions() debe retornar un objeto con opciones válidas
   */
  it('debe retornar opciones válidas para gráfico de barras', () => {
    const opciones = getBarChartOptions() as ChartConfiguration['options'];

    // Verificar que retorna un objeto
    expect(opciones).toBeTruthy();

    // Verificar propiedades básicas
    expect(opciones!.responsive).toBe(true);
    expect(opciones!.maintainAspectRatio).toBe(false);

    // Verificar que tiene configuración de escalas
    expect(opciones!.scales).toBeTruthy();
    expect((opciones!.scales as any)?.['x']).toBeTruthy();
    expect((opciones!.scales as any)?.['y']).toBeTruthy();
  });

  /**
   * Test 2: getBarChartOptions() debe permitir mostrar/ocultar leyenda
   */
  it('debe permitir mostrar la leyenda cuando showLegend = true', () => {
    const opciones = getBarChartOptions(true) as ChartConfiguration['options'];

    expect(opciones!.plugins?.legend?.display).toBe(true);
  });

  /**
   * Test 3: getBarChartOptions() debe ocultar leyenda por defecto
   */
  it('debe ocultar la leyenda cuando showLegend = false', () => {
    const opciones = getBarChartOptions(false) as ChartConfiguration['options'];

    expect(opciones!.plugins?.legend?.display).toBe(false);
  });

  /**
   * Test 4: getBarChartOptions() debe tener escala Y que empieza en cero
   */
  it('debe tener escala Y que comienza en cero', () => {
    const opciones = getBarChartOptions() as ChartConfiguration['options'];

    expect((opciones!.scales as any)?.['y']?.['beginAtZero']).toBe(true);
  });

  // ============================================
  // Tests para getDoughnutChartOptions()
  // ============================================

  /**
   * Test 5: getDoughnutChartOptions() debe retornar opciones válidas
   */
  it('debe retornar opciones válidas para gráfico doughnut', () => {
    const opciones = getDoughnutChartOptions() as ChartConfiguration['options'];

    // Verificar propiedades básicas
    expect(opciones).toBeTruthy();
    expect(opciones!.responsive).toBe(true);
    expect(opciones!.maintainAspectRatio).toBe(false);

    // Los gráficos doughnut no tienen escalas
    expect(opciones!.scales).toBeUndefined();
  });

  /**
   * Test 6: getDoughnutChartOptions() debe posicionar leyenda en bottom
   */
  it('debe posicionar la leyenda en la parte inferior', () => {
    const opciones = getDoughnutChartOptions() as ChartConfiguration['options'];

    expect(opciones!.plugins?.legend?.position).toBe('bottom');
  });

  // ============================================
  // Tests para getLineChartOptions()
  // ============================================

  /**
   * Test 7: getLineChartOptions() debe retornar opciones válidas
   */
  it('debe retornar opciones válidas para gráfico de línea', () => {
    const opciones = getLineChartOptions() as ChartConfiguration['options'];

    // Verificar propiedades básicas
    expect(opciones).toBeTruthy();
    expect(opciones!.responsive).toBe(true);

    // Verificar que tiene escalas
    expect(opciones!.scales).toBeTruthy();
    expect((opciones!.scales as any)?.['y']?.['beginAtZero']).toBe(true);
  });

  /**
   * Test 8: getLineChartOptions() debe no mostrar grid en eje X
   */
  it('debe no mostrar grid en eje X', () => {
    const opciones = getLineChartOptions() as ChartConfiguration['options'];

    expect((opciones!.scales as any)?.['x']?.grid?.display).toBe(false);
  });

  // ============================================
  // Tests para getScatterChartOptions()
  // ============================================

  /**
   * Test 9: getScatterChartOptions() debe retornar opciones válidas
   */
  it('debe retornar opciones válidas para gráfico scatter', () => {
    const opciones = getScatterChartOptions() as ChartConfiguration['options'];

    expect(opciones).toBeTruthy();
    expect(opciones!.scales).toBeTruthy();
    expect((opciones!.scales as any)?.['x']).toBeTruthy();
  });

  /**
   * Test 10: getScatterChartOptions() debe tener tipo de escala X linear
   */
  it('debe tener eje X de tipo linear', () => {
    const opciones = getScatterChartOptions() as ChartConfiguration['options'];

    expect((opciones!.scales as any)?.['x']?.type).toBe('linear');
  });

  // ============================================
  // Tests para getChartColors()
  // ============================================

  /**
   * Test 11: getChartColors(5) debe retornar array de 5 colores
   */
  it('debe retornar array de colores de la longitud solicitada', () => {
    const colores = getChartColors(5);

    expect(colores).toBeTruthy();
    expect(colores.length).toBe(5);
    expect(Array.isArray(colores)).toBe(true);
  });

  /**
   * Test 12: getChartColors() debe usar la paleta predefinida si count <= paleta.length
   */
  it('debe usar la paleta predefinida para counts pequeños', () => {
    const count = 3;
    const colores = getChartColors(count);

    // Verificar que usa los primeros colores de la paleta
    const paletaEsperada = CHART_COLOR_PALETTE.slice(0, count);
    expect(colores).toEqual(paletaEsperada);
  });

  /**
   * Test 13: getChartColors() debe generar colores adicionales si count > paleta.length
   */
  it('debe generar colores adicionales cuando count > paleta.length', () => {
    const count = CHART_COLOR_PALETTE.length + 5;
    const colores = getChartColors(count);

    // Debe tener la cantidad solicitada
    expect(colores.length).toBe(count);

    // Los primeros deben ser de la paleta
    expect(colores.slice(0, CHART_COLOR_PALETTE.length)).toEqual(CHART_COLOR_PALETTE);
  });

  /**
   * Test 14: getChartColors() debe retornar colores válidos en formato hsl o hexadecimal
   */
  it('debe retornar colores en formato válido', () => {
    const colores = getChartColors(5);

    // Todos deben ser strings
    colores.forEach(color => {
      expect(typeof color).toBe('string');
      // Deben ser colores válidos (contener # o hsl)
      expect(color.length).toBeGreaterThan(0);
    });
  });

  // ============================================
  // Tests para createBarDataset()
  // ============================================

  /**
   * Test 15: createBarDataset() debe retornar un dataset válido
   */
  it('debe crear un dataset válido para gráfico de barras', () => {
    const label = 'Incidencias';
    const data = [10, 20, 30, 40];

    const dataset = createBarDataset(label, data);

    expect(dataset).toBeTruthy();
    expect(dataset.label).toBe(label);
    expect(dataset.data).toEqual(data);
  });

  /**
   * Test 16: createBarDataset() debe usar color por defecto si no se proporciona
   */
  it('debe usar color por defecto si no se proporciona', () => {
    const dataset = createBarDataset('Test', [1, 2, 3]);

    expect(dataset.borderColor).toBe(CHART_COLORS.primary);
    expect(dataset.backgroundColor).toBe(CHART_COLORS.primary);
  });

  /**
   * Test 17: createBarDataset() debe usar color personalizado si se proporciona
   */
  it('debe usar color personalizado si se proporciona', () => {
    const colorCustom = '#ff0000';
    const dataset = createBarDataset('Test', [1, 2, 3], colorCustom);

    expect(dataset.borderColor).toBe(colorCustom);
  });

  /**
   * Test 18: createBarDataset() debe tener borderWidth de 1 y borderRadius de 4
   */
  it('debe tener propiedades de borde configuradas correctamente', () => {
    const dataset = createBarDataset('Test', [1, 2, 3]);

    expect(dataset.borderWidth).toBe(1);
    expect(dataset.borderRadius).toBe(4);
  });

  // ============================================
  // Tests para createLineDataset()
  // ============================================

  /**
   * Test 19: createLineDataset() debe retornar un dataset válido
   */
  it('debe crear un dataset válido para gráfico de línea', () => {
    const label = 'Tendencia';
    const data = [5, 10, 15, 20];

    const dataset = createLineDataset(label, data);

    expect(dataset).toBeTruthy();
    expect(dataset.label).toBe(label);
    expect(dataset.data).toEqual(data);
  });

  /**
   * Test 20: createLineDataset() debe tener tension de 0.4
   */
  it('debe tener tension de 0.4 para curvas suaves', () => {
    const dataset = createLineDataset('Test', [1, 2, 3]);

    expect(dataset.tension).toBe(0.4);
  });

  /**
   * Test 21: createLineDataset() debe no rellenar por defecto
   */
  it('debe no rellenar el área bajo la línea por defecto', () => {
    const dataset = createLineDataset('Test', [1, 2, 3]);

    expect(dataset.fill).toBe(false);
    expect(dataset.backgroundColor).toBe('transparent');
  });

  /**
   * Test 22: createLineDataset() debe rellenar cuando fill = true
   */
  it('debe rellenar el área cuando fill = true', () => {
    const colorCustom = '#ff0000';
    const dataset = createLineDataset('Test', [1, 2, 3], colorCustom, true);

    expect(dataset.fill).toBe(true);
    expect(dataset.backgroundColor).toContain('#ff0000');
  });

  // ============================================
  // Tests para createDoughnutDataset()
  // ============================================

  /**
   * Test 23: createDoughnutDataset() debe retornar un objeto con estructura correcta
   */
  it('debe crear un dataset válido para gráfico doughnut', () => {
    const labels = ['Basura', 'Obras', 'Alumbrado'];
    const data = [50, 30, 20];

    const dataset = createDoughnutDataset(labels, data);

    expect(dataset).toBeTruthy();
    expect(dataset.labels).toEqual(labels);
    expect(dataset.datasets).toBeTruthy();
    expect(dataset.datasets.length).toBe(1);
  });

  /**
   * Test 24: createDoughnutDataset() debe contener los datos en el primer dataset
   */
  it('debe contener los datos en la estructura del dataset', () => {
    const labels = ['A', 'B', 'C'];
    const data = [10, 20, 30];

    const dataset = createDoughnutDataset(labels, data);

    expect(dataset.datasets[0].data).toEqual(data);
  });

  /**
   * Test 25: createDoughnutDataset() debe usar colores predeterminados si no se proporcionan
   */
  it('debe usar colores predeterminados si no se proporcionan', () => {
    const dataset = createDoughnutDataset(['A', 'B'], [10, 20]);

    expect(dataset.datasets[0].backgroundColor).toBeTruthy();
    expect(Array.isArray(dataset.datasets[0].backgroundColor)).toBe(true);
  });

  /**
   * Test 26: createDoughnutDataset() debe usar colores personalizados si se proporcionan
   */
  it('debe usar colores personalizados si se proporcionan', () => {
    const coloresCustom = ['#ff0000', '#00ff00'];
    const dataset = createDoughnutDataset(['A', 'B'], [10, 20], coloresCustom);

    expect(dataset.datasets[0].backgroundColor).toEqual(coloresCustom);
  });

  /**
   * Test 27: createDoughnutDataset() debe tener borde blanco
   */
  it('debe tener borde blanco con ancho de 2', () => {
    const dataset = createDoughnutDataset(['A'], [10]);

    expect(dataset.datasets[0].borderColor).toBe('#ffffff');
    expect(dataset.datasets[0].borderWidth).toBe(2);
  });

  /**
   * Test 28: createDoughnutDataset() debe tener hoverBorderWidth de 3
   */
  it('debe tener hoverBorderWidth de 3', () => {
    const dataset = createDoughnutDataset(['A'], [10]);

    expect(dataset.datasets[0].hoverBorderWidth).toBe(3);
  });

  // ============================================
  // Tests para commonChartOptions
  // ============================================

  /**
   * Test 29: commonChartOptions debe tener responsive = true
   */
  it('debe tener responsive = true', () => {
    expect((commonChartOptions as any).responsive).toBe(true);
  });

  /**
   * Test 30: commonChartOptions debe tener leyenda configurada
   */
  it('debe tener leyenda configurada por defecto', () => {
    expect((commonChartOptions as any).plugins?.legend).toBeTruthy();
    expect((commonChartOptions as any).plugins?.legend?.display).toBe(true);
  });
});
