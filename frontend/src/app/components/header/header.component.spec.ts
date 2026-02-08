import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';
import { HeaderComponent } from './header.component';

/**
 * Tests unitarios para HeaderComponent
 * Valida la funcionalidad de mostrar status, lastUpdated y botón de actualización
 */
describe('HeaderComponent', () => {
  let component: HeaderComponent;
  let fixture: ComponentFixture<HeaderComponent>;
  let debugElement: DebugElement;

  beforeEach(async () => {
    // Configurar el módulo de prueba con el componente standalone
    await TestBed.configureTestingModule({
      imports: [HeaderComponent]
    }).compileComponents();

    // Crear la instancia del componente
    fixture = TestBed.createComponent(HeaderComponent);
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
   * Test 2: Verificar que muestra el estado "loading"
   */
  it('debe mostrar status "loading" cuando status = loading', () => {
    // Asignar el valor de entrada
    component.status = 'loading';
    
    // Detectar cambios en la vista
    fixture.detectChanges();

    // Buscar el elemento de status en la plantilla
    const statusElement = debugElement.query(By.css('.header__status--loading'));

    // Verificar que existe y contiene el texto esperado
    expect(statusElement).toBeTruthy();
    expect(statusElement.nativeElement.textContent).toContain('Cargando datos...');
  });

  /**
   * Test 3: Verificar que muestra el estado "ready"
   */
  it('debe mostrar status "ready" cuando status = ready', () => {
    component.status = 'ready';
    fixture.detectChanges();

    const statusElement = debugElement.query(By.css('.header__status--ready'));

    expect(statusElement).toBeTruthy();
    expect(statusElement.nativeElement.textContent).toContain('Datos sincronizados');
  });

  /**
   * Test 4: Verificar que muestra el estado "error"
   */
  it('debe mostrar status "error" cuando status = error', () => {
    component.status = 'error';
    fixture.detectChanges();

    const statusElement = debugElement.query(By.css('.header__status--error'));

    expect(statusElement).toBeTruthy();
    expect(statusElement.nativeElement.textContent).toContain('Error de conexión');
  });

  /**
   * Test 5: Verificar que muestra "Pendiente" cuando lastUpdated es null
   */
  it('debe mostrar "Pendiente" cuando lastUpdated es null', () => {
    component.lastUpdated = null;
    fixture.detectChanges();

    const timeElement = debugElement.query(By.css('.header__time'));

    expect(timeElement).toBeTruthy();
    expect(timeElement.nativeElement.textContent).toContain('Pendiente');
  });

  /**
   * Test 6: Verificar que muestra lastUpdated cuando está seteado
   */
  it('debe mostrar lastUpdated cuando está seteado', () => {
    const testTime = '14:30:45';
    component.lastUpdated = testTime;
    fixture.detectChanges();

    const timeElement = debugElement.query(By.css('.header__time'));

    expect(timeElement).toBeTruthy();
    expect(timeElement.nativeElement.textContent).toContain(testTime);
  });

  /**
   * Test 7: Verificar que el botón "Actualizar" es visible y clickeable
   */
  it('debe mostrar el botón Actualizar', () => {
    fixture.detectChanges();

    const button = debugElement.query(By.css('.header__button'));

    expect(button).toBeTruthy();
    expect(button.nativeElement.textContent).toContain('Actualizar métricas');
  });

  /**
   * Test 8: Verificar que se llama a onRefresh() al hacer click en el botón
   */
  it('debe llamar a onRefresh() cuando se hace click en el botón', () => {
    // Espiar el método onRefresh
    spyOn(component, 'onRefresh');
    
    fixture.detectChanges();

    // Encontrar y hacer click en el botón
    const button = debugElement.query(By.css('.header__button'));
    button.nativeElement.click();

    // Verificar que el método fue llamado
    expect(component.onRefresh).toHaveBeenCalled();
  });
});
