import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';
import { CardComponent } from './card.component';

/**
 * Tests unitarios para CardComponent
 * Valida la funcionalidad del componente contenedor reutilizable
 */
describe('CardComponent', () => {
  let component: CardComponent;
  let fixture: ComponentFixture<CardComponent>;
  let debugElement: DebugElement;

  beforeEach(async () => {
    // Configurar el módulo de prueba con el componente standalone
    await TestBed.configureTestingModule({
      imports: [CardComponent]
    }).compileComponents();

    // Crear la instancia del componente
    fixture = TestBed.createComponent(CardComponent);
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
   * Test 2: Verificar que muestra el title cuando está seteado
   */
  it('debe mostrar el title cuando está seteado', () => {
    const testTitle = 'Gráfico de Incidencias';
    component.title = testTitle;
    fixture.detectChanges();

    // Buscar el elemento del título
    const titleElement = debugElement.query(By.css('.card__title'));

    expect(titleElement).toBeTruthy();
    expect(titleElement.nativeElement.textContent).toContain(testTitle);
  });

  /**
   * Test 3: Verificar que no muestra el header cuando title es null
   */
  it('no debe mostrar el header cuando title es null', () => {
    component.title = null;
    fixture.detectChanges();

    const headerElement = debugElement.query(By.css('.card__header'));

    expect(headerElement).toBeFalsy();
  });

  /**
   * Test 4: Verificar que muestra el subtitle cuando está seteado
   */
  it('debe mostrar el subtitle cuando está seteado', () => {
    const testTitle = 'Incidencias por tipo';
    const testSubtitle = 'Datos de los últimos 30 días';
    component.title = testTitle;
    component.subtitle = testSubtitle;
    fixture.detectChanges();

    // Buscar el elemento del subtítulo
    const subtitleElement = debugElement.query(By.css('.card__subtitle'));

    expect(subtitleElement).toBeTruthy();
    expect(subtitleElement.nativeElement.textContent).toContain(testSubtitle);
  });

  /**
   * Test 5: Verificar que muestra loading spinner cuando loading=true
   */
  it('debe mostrar loading spinner cuando loading=true', () => {
    component.loading = true;
    fixture.detectChanges();

    // Buscar el elemento de carga
    const loadingElement = debugElement.query(By.css('.card__loading'));
    const spinner = debugElement.query(By.css('.card__spinner'));

    expect(loadingElement).toBeTruthy();
    expect(spinner).toBeTruthy();
    expect(loadingElement.nativeElement.textContent).toContain('Cargando datos...');
  });

  /**
   * Test 6: Verificar que NO muestra loading spinner cuando loading=false
   */
  it('no debe mostrar loading spinner cuando loading=false', () => {
    component.loading = false;
    fixture.detectChanges();

    const loadingElement = debugElement.query(By.css('.card__loading'));

    expect(loadingElement).toBeFalsy();
  });

  /**
   * Test 7: Verificar que muestra error message cuando error está seteado
   */
  it('debe mostrar error message cuando error está seteado', () => {
    const errorMessage = 'Error al cargar los datos';
    component.error = errorMessage;
    fixture.detectChanges();

    // Buscar el elemento de error
    const errorElement = debugElement.query(By.css('.card__error'));

    expect(errorElement).toBeTruthy();
    expect(errorElement.nativeElement.textContent).toContain(errorMessage);
  });

  /**
   * Test 8: Verificar que NO muestra error cuando error es null
   */
  it('no debe mostrar error cuando error es null', () => {
    component.error = null;
    fixture.detectChanges();

    const errorElement = debugElement.query(By.css('.card__error'));

    expect(errorElement).toBeFalsy();
  });

  /**
   * Test 9: Verificar que se aplica la clase "card--wide" cuando wide=true
   */
  it('debe aplicar la clase card--wide cuando wide=true', () => {
    component.wide = true;
    fixture.detectChanges();

    // Buscar el elemento de la tarjeta
    const cardElement = debugElement.query(By.css('.card'));

    expect(cardElement.nativeElement.classList.contains('card--wide')).toBe(true);
  });

  /**
   * Test 10: Verificar que NO aplica la clase "card--wide" cuando wide=false
   */
  it('no debe aplicar la clase card--wide cuando wide=false', () => {
    component.wide = false;
    fixture.detectChanges();

    const cardElement = debugElement.query(By.css('.card'));

    expect(cardElement.nativeElement.classList.contains('card--wide')).toBe(false);
  });

  /**
   * Test 11: Verificar que el contenido proyectado (ng-content) se muestra
   */
  it('debe proyectar contenido mediante ng-content', () => {
    // Crear un componente con contenido para proyectar
    @Component({
      standalone: true,
      imports: [CardComponent],
      template: `
        <app-card title="Test Card">
          <p id="test-content">Contenido proyectado</p>
        </app-card>
      `
    })
    class TestHostComponent {}

    // Crear fixture con el componente host
    const testFixture = TestBed.createComponent(TestHostComponent);
    testFixture.detectChanges();

    // Buscar el contenido proyectado
    const projectedContent = testFixture.debugElement.query(By.css('#test-content'));

    expect(projectedContent).toBeTruthy();
    expect(projectedContent.nativeElement.textContent).toContain('Contenido proyectado');
  });
});

/**
 * Componente de prueba para la proyección de contenido
 */
import { Component } from '@angular/core';
