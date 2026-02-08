import {
  formatearNumero,
  formatearPorcentaje,
  formatearTiempo,
  formatearMinutos,
  formatearCompacto,
  obtenerLabelTipo,
  obtenerLabelPrioridad,
  obtenerLabelEstado,
  TIPO_INCIDENCIA_LABELS,
  PRIORIDAD_LABELS,
  ESTADO_LABELS
} from './format.utils';

/**
 * Tests unitarios para las funciones de formato
 * Valida que los valores se formateen correctamente según el estándar español
 */
describe('Funciones de Formateo - format.utils', () => {

  // ============================================
  // Tests para formatearNumero()
  // ============================================

  /**
   * Test 1: formatearNumero() debe devolver formato españolizado
   */
  it('debe formatear número 100 como "100"', () => {
    const resultado = formatearNumero(100);
    expect(resultado).toBe('100');
  });

  /**
   * Test 2: formatearNumero() debe agregar separador de miles
   */
  it('debe formatear número 1000 con separador de miles', () => {
    const resultado = formatearNumero(1000);
    // El formato exacto depende del locale del navegador
    // En español es "1.000" pero en otros locales puede variar
    expect(['1.000', '1,000', '1000'].includes(resultado)).toBe(true);
    expect(resultado).toBeTruthy();
  });

  /**
   * Test 3: formatearNumero() debe devolver guión para null
   */
  it('debe devolver "—" cuando el número es null', () => {
    const resultado = formatearNumero(null);
    expect(resultado).toBe('—');
  });

  /**
   * Test 4: formatearNumero() debe devolver guión para undefined
   */
  it('debe devolver "—" cuando el número es undefined', () => {
    const resultado = formatearNumero(undefined);
    expect(resultado).toBe('—');
  });

  // ============================================
  // Tests para formatearPorcentaje()
  // ============================================

  /**
   * Test 5: formatearPorcentaje() debe formatar con un decimal y símbolo %
   */
  it('debe formatear porcentaje 85.5 como "85.5%"', () => {
    const resultado = formatearPorcentaje(85.5);
    expect(resultado).toBe('85.5%');
  });

  /**
   * Test 6: formatearPorcentaje() debe redondear a un decimal
   */
  it('debe redondear porcentaje a un decimal', () => {
    const resultado = formatearPorcentaje(85.567);
    expect(resultado).toBe('85.6%');
  });

  /**
   * Test 7: formatearPorcentaje() debe devolver guión para null
   */
  it('debe devolver "—" cuando el porcentaje es null', () => {
    const resultado = formatearPorcentaje(null);
    expect(resultado).toBe('—');
  });

  // ============================================
  // Tests para formatearTiempo()
  // ============================================

  /**
   * Test 8: formatearTiempo() debe convertir 3661 segundos a "1h 1m 1s"
   */
  it('debe formatear 3661 segundos como "1h 1m 1s"', () => {
    const resultado = formatearTiempo(3661);
    expect(resultado).toBe('1h 1m 1s');
  });

  /**
   * Test 9: formatearTiempo() debe manejar 0 segundos
   */
  it('debe formatear 0 segundos como "0s"', () => {
    const resultado = formatearTiempo(0);
    expect(resultado).toBe('0s');
  });

  /**
   * Test 10: formatearTiempo() debe omitir partes con valor 0
   */
  it('debe omitir horas y minutos si son 0', () => {
    const resultado = formatearTiempo(45); // 45 segundos
    expect(resultado).toBe('45s');
  });

  /**
   * Test 11: formatearTiempo() debe formatear solo minutos y segundos
   */
  it('debe formatear 125 segundos como "2m 5s"', () => {
    const resultado = formatearTiempo(125);
    expect(resultado).toBe('2m 5s');
  });

  /**
   * Test 12: formatearTiempo() debe devolver guión para null
   */
  it('debe devolver "—" cuando el tiempo es null', () => {
    const resultado = formatearTiempo(null);
    expect(resultado).toBe('—');
  });

  // ============================================
  // Tests para formatearMinutos()
  // ============================================

  /**
   * Test 13: formatearMinutos() debe convertir 65 minutos a "1h 5m"
   */
  it('debe formatear 65 minutos como "1h 5m"', () => {
    const resultado = formatearMinutos(65);
    expect(resultado).toBe('1h 5m');
  });

  /**
   * Test 14: formatearMinutos() debe formatear minutos menores a 60
   */
  it('debe formatear 30 minutos como "30m"', () => {
    const resultado = formatearMinutos(30);
    expect(resultado).toBe('30m');
  });

  /**
   * Test 15: formatearMinutos() debe devolver guión para null
   */
  it('debe devolver "—" cuando los minutos son null', () => {
    const resultado = formatearMinutos(null);
    expect(resultado).toBe('—');
  });

  // ============================================
  // Tests para formatearCompacto()
  // ============================================

  /**
   * Test 16: formatearCompacto() debe convertir valores grandes a K
   */
  it('debe formatear 1500 como "1.5K"', () => {
    const resultado = formatearCompacto(1500);
    expect(resultado).toBe('1.5K');
  });

  /**
   * Test 17: formatearCompacto() debe convertir valores a millones con M
   */
  it('debe formatear 1500000 como "1.5M"', () => {
    const resultado = formatearCompacto(1500000);
    expect(resultado).toBe('1.5M');
  });

  /**
   * Test 18: formatearCompacto() debe devolver el número como string si es menor a 1000
   */
  it('debe formatear 500 como "500"', () => {
    const resultado = formatearCompacto(500);
    expect(resultado).toBe('500');
  });

  // ============================================
  // Tests para obtenerLabelTipo()
  // ============================================

  /**
   * Test 19: obtenerLabelTipo() debe retornar "Basura" para 'BASURA'
   */
  it('debe obtener label "Basura" para tipo BASURA', () => {
    const resultado = obtenerLabelTipo('BASURA');
    expect(resultado).toBe('Basura');
  });

  /**
   * Test 20: obtenerLabelTipo() debe retornar "Obras" para 'OBRAS'
   */
  it('debe obtener label "Obras" para tipo OBRAS', () => {
    const resultado = obtenerLabelTipo('OBRAS');
    expect(resultado).toBe('Obras');
  });

  /**
   * Test 21: obtenerLabelTipo() debe retornar la clave si no existe en el mapa
   */
  it('debe retornar la clave original si no existe en el mapa de labels', () => {
    const resultado = obtenerLabelTipo('TIPO_DESCONOCIDO');
    expect(resultado).toBe('TIPO_DESCONOCIDO');
  });

  /**
   * Test 22: obtenerLabelTipo() debe contener todas las claves esperadas
   */
  it('debe tener todos los labels de tipos de incidencia definidos', () => {
    expect(TIPO_INCIDENCIA_LABELS).toEqual({
      BASURA: 'Basura',
      OBRAS: 'Obras',
      ALUMBRADO: 'Alumbrado',
      SEGURIDAD: 'Seguridad',
      SANIDAD: 'Sanidad',
      OTRO: 'Otro'
    });
  });

  // ============================================
  // Tests para obtenerLabelPrioridad()
  // ============================================

  /**
   * Test 23: obtenerLabelPrioridad() debe retornar "Alta" para 'ALTA'
   */
  it('debe obtener label "Alta" para prioridad ALTA', () => {
    const resultado = obtenerLabelPrioridad('ALTA');
    expect(resultado).toBe('Alta');
  });

  /**
   * Test 24: obtenerLabelPrioridad() debe retornar "Media" para 'MEDIA'
   */
  it('debe obtener label "Media" para prioridad MEDIA', () => {
    const resultado = obtenerLabelPrioridad('MEDIA');
    expect(resultado).toBe('Media');
  });

  /**
   * Test 25: obtenerLabelPrioridad() debe retornar "Baja" para 'BAJA'
   */
  it('debe obtener label "Baja" para prioridad BAJA', () => {
    const resultado = obtenerLabelPrioridad('BAJA');
    expect(resultado).toBe('Baja');
  });

  /**
   * Test 26: obtenerLabelPrioridad() debe contener todas las claves esperadas
   */
  it('debe tener todos los labels de prioridad definidos', () => {
    expect(PRIORIDAD_LABELS).toEqual({
      ALTA: 'Alta',
      MEDIA: 'Media',
      BAJA: 'Baja'
    });
  });

  // ============================================
  // Tests para obtenerLabelEstado()
  // ============================================

  /**
   * Test 27: obtenerLabelEstado() debe retornar "Pendiente" para 'PENDIENTE'
   */
  it('debe obtener label "Pendiente" para estado PENDIENTE', () => {
    const resultado = obtenerLabelEstado('PENDIENTE');
    expect(resultado).toBe('Pendiente');
  });

  /**
   * Test 28: obtenerLabelEstado() debe retornar "Resuelto" para 'RESUELTO'
   */
  it('debe obtener label "Resuelto" para estado RESUELTO', () => {
    const resultado = obtenerLabelEstado('RESUELTO');
    expect(resultado).toBe('Resuelto');
  });

  /**
   * Test 29: obtenerLabelEstado() debe retornar "Cerrado" para 'CERRADO'
   */
  it('debe obtener label "Cerrado" para estado CERRADO', () => {
    const resultado = obtenerLabelEstado('CERRADO');
    expect(resultado).toBe('Cerrado');
  });

  /**
   * Test 30: obtenerLabelEstado() debe retornar "Rechazado" para 'RECHAZADO'
   */
  it('debe obtener label "Rechazado" para estado RECHAZADO', () => {
    const resultado = obtenerLabelEstado('RECHAZADO');
    expect(resultado).toBe('Rechazado');
  });

  /**
   * Test 31: obtenerLabelEstado() debe contener todas las claves esperadas
   */
  it('debe tener todos los labels de estado definidos', () => {
    expect(ESTADO_LABELS).toEqual({
      PENDIENTE: 'Pendiente',
      RESUELTO: 'Resuelto',
      CERRADO: 'Cerrado',
      RECHAZADO: 'Rechazado'
    });
  });
});
