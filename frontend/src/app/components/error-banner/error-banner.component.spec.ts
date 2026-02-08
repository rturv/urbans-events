import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';
import { ErrorBannerComponent } from './error-banner.component';
import { ErrorState } from '../../types/metricas.types';

/**
 * Tests unitarios para ErrorBannerComponent
 * Valida la funcionalidad de mostrar y ocultar errores
 */
describe('ErrorBannerComponent', () => {
  let component: ErrorBannerComponent;
  let fixture: ComponentFixture<ErrorBannerComponent>;
  let debugElement: DebugElement;

  beforeEach(async () => {
    // Configurar el módulo de prueba con el componente standalone
    await TestBed.configureTestingModule({
      imports: [ErrorBannerComponent]
    }).compileComponents();

    // Crear la instancia del componente
    fixture = TestBed.createComponent(ErrorBannerComponent);
    component = fixture.componentInstance;
    debugElement = fixture.debugElement;
  });

  /**
   * Test 1: Verificar que el componente se crea correctamente
   */
  it('debe crear el componente', () => {
    expect(component).toBeTruthy();
  });

  /**
   * Test 2: Verificar que muestra el banner cuando error.hasError = true
   */
  it('debe mostrar el banner cuando error.hasError = true', () => {
    // Crear un estado de error válido
    const errorState: ErrorState = {
      hasError: true,
      message: 'Error de conexión al servidor',
      timestamp: new Date()
    };
    
    component.error = errorState;
    fixture.detectChanges();

    // Buscar el banner de error
    const banner = debugElement.query(By.css('.error-banner'));

    expect(banner).toBeTruthy();
  });

  /**
   * Test 3: Verificar que NO muestra el banner cuando error.hasError = false
   */
  it('no debe mostrar el banner cuando error.hasError = false', () => {
    const errorState: ErrorState = {
      hasError: false,
      message: '',
      timestamp: new Date()
    };
    
    component.error = errorState;
    fixture.detectChanges();

    const banner = debugElement.query(By.css('.error-banner'));

    expect(banner).toBeFalsy();
  });

  /**
   * Test 4: Verificar que NO muestra el banner cuando error es null
   */
  it('no debe mostrar el banner cuando error es null', () => {
    component.error = null;
    fixture.detectChanges();

    const banner = debugElement.query(By.css('.error-banner'));

    expect(banner).toBeFalsy();
  });

  /**
   * Test 5: Verificar que se muestra el mensaje de error
   */
  it('debe mostrar el mensaje de error', () => {
    const mensajeError = 'Error crítico: base de datos no disponible';
    
    const errorState: ErrorState = {
      hasError: true,
      message: mensajeError,
      timestamp: new Date()
    };
    
    component.error = errorState;
    fixture.detectChanges();

    // Buscar el elemento que contiene el mensaje
    const messageDetail = debugElement.query(By.css('.error-banner__detail'));

    expect(messageDetail).toBeTruthy();
    expect(messageDetail.nativeElement.textContent).toContain(mensajeError);
  });

  /**
   * Test 6: Verificar que el banner contiene ícono, botón cerrar y mensaje
   */
  it('debe mostrar ícono, botón cerrar y mensaje de error', () => {
    const errorState: ErrorState = {
      hasError: true,
      message: 'Prueba de error',
      timestamp: new Date()
    };
    
    component.error = errorState;
    fixture.detectChanges();

    // Verificar que existe el ícono
    const icon = debugElement.query(By.css('.error-banner__icon'));
    expect(icon).toBeTruthy();
    expect(icon.nativeElement.textContent).toContain('⚠️');

    // Verificar que existe el botón de cerrar
    const closeButton = debugElement.query(By.css('.error-banner__close'));
    expect(closeButton).toBeTruthy();
    expect(closeButton.nativeElement.textContent).toContain('✕');

    // Verificar que existe el mensaje
    const title = debugElement.query(By.css('.error-banner__title'));
    expect(title).toBeTruthy();
    expect(title.nativeElement.textContent).toContain('Error al conectar con métricas');
  });

  /**
   * Test 7: Verificar que onClose() limpia el error (establece hasError = false)
   */
  it('debe llamar a onClose() cuando se hace click en el botón de cerrar', () => {
    const errorState: ErrorState = {
      hasError: true,
      message: 'Error de prueba',
      timestamp: new Date()
    };
    
    component.error = errorState;
    fixture.detectChanges();

    // Espiar el método onClose
    spyOn(component, 'onClose');

    // Encontrar y hacer click en el botón de cerrar
    const closeButton = debugElement.query(By.css('.error-banner__close'));
    closeButton.nativeElement.click();

    // Verificar que se llamó al método
    expect(component.onClose).toHaveBeenCalled();
  });

  /**
   * Test 8: Verificar que onClose() modifica el estado de error
   */
  it('debe establecer hasError = false cuando se llama a onClose()', () => {
    const errorState: ErrorState = {
      hasError: true,
      message: 'Error de prueba',
      timestamp: new Date()
    };
    
    component.error = errorState;
    
    // Llamar directamente a onClose
    component.onClose();

    // Verificar que hasError cambió a false
    expect(component.error.hasError).toBe(false);
  });
});
