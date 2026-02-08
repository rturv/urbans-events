import { TestBed } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController
} from '@angular/common/http/testing';

import { MetricasService } from './metricas.service';
import { ConfigService } from './config.service';
import {
  ResumenMetricas,
  MetricaAgregada,
  EstadisticasTipo,
  IncidenciaPendiente
} from '../models';

describe('MetricasService', () => {
  let service: MetricasService;
  let httpMock: HttpTestingController;
  let configService: ConfigService;

  const mockResumen: ResumenMetricas = {
    totalIncidencias: 100,
    incidenciasResueltas: 80,
    incidenciasPendientes: 15,
    incidenciasRechazadas: 5,
    tasaExitoPct: 80,
    tasaFracasoPct: 5,
    tasaPendientePct: 15,
    tiempoPromedioResolucionSeg: 3600,
    tiempoPromedioResolucionMin: 60
  };

  const mockAgregadas: MetricaAgregada[] = [
    {
      tipoIncidencia: 'BASURA',
      prioridad: 'ALTA',
      cantidadTotal: 50,
      cantidadResuelta: 40,
      cantidadPendiente: 8,
      cantidadRechazada: 2,
      tasaExitoPct: 80,
      tasaFracasoPct: 4,
      tasaPendientePct: 16,
      tiempoPromedioResolucionSeg: 1800,
      tiempoMinResolucionSeg: 300,
      tiempoMaxResolucionSeg: 3600,
      p50Seg: 1500,
      p95Seg: 3300,
      p99Seg: 3500
    }
  ];

  const mockEstadisticas: EstadisticasTipo[] = [
    {
      tipo: 'BASURA',
      cantidad: 50,
      cantidadResuelta: 40,
      cantidadPendiente: 8,
      cantidadRechazada: 2,
      tasaExitoPct: 80,
      tiempoPromedioResolucionSeg: 1800
    }
  ];

  const mockPendientes: IncidenciaPendiente[] = [
    {
      incidenciaId: 1,
      tipo: 'BASURA',
      prioridad: 'ALTA',
      estado: 'PENDIENTE',
      tiempoDesdeCreacionSeg: 1200,
      tiempoDesdeCreacionMin: 20
    }
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [MetricasService, ConfigService]
    });

    service = TestBed.inject(MetricasService);
    httpMock = TestBed.inject(HttpTestingController);
    configService = TestBed.inject(ConfigService);

    // Limpiar caché antes de cada test
    service.clearCache();
  });

  afterEach(() => {
    httpMock.verify();
  });

  describe('obtenerResumen', () => {
    it('debería obtener resumen exitosamente', (done) => {
      service.obtenerResumen().subscribe((data) => {
        expect(data).toEqual(mockResumen);
        expect(data.totalIncidencias).toBe(100);
        done();
      });

      const req = httpMock.expectOne(
        `${configService.apiBaseUrl}/resumen`
      );
      expect(req.request.method).toBe('GET');
      req.flush(mockResumen);
    });

    it('debería cachear el resumen en 60 segundos', (done) => {
      service.obtenerResumen().subscribe(() => {
        // Segunda llamada
        service.obtenerResumen().subscribe(() => {
          done();
        });

        // No debe haber segunda solicitud HTTP (está en caché)
        httpMock.expectNone(`${configService.apiBaseUrl}/resumen`);
      });

      const req = httpMock.expectOne(
        `${configService.apiBaseUrl}/resumen`
      );
      req.flush(mockResumen);
    });

    it('debería manejar errores HTTP', (done) => {
      service.obtenerResumen().subscribe({
        next: () => fail('debería haber fallado'),
        error: (error) => {
          expect(error.message).toContain('Error');
          done();
        }
      });

      const req = httpMock.expectOne(
        `${configService.apiBaseUrl}/resumen`
      );
      req.error(new ErrorEvent('Network error'));
    });
  });

  describe('obtenerAgregadas', () => {
    it('debería obtener agregadas sin filtros', (done) => {
      service.obtenerAgregadas().subscribe((data) => {
        expect(data).toEqual(mockAgregadas);
        done();
      });

      const req = httpMock.expectOne(
        `${configService.apiBaseUrl}/agregadas`
      );
      expect(req.request.method).toBe('GET');
      req.flush(mockAgregadas);
    });

    it('debería obtener agregadas con filtro tipo', (done) => {
      service.obtenerAgregadas('BASURA').subscribe((data) => {
        expect(data).toEqual(mockAgregadas);
        done();
      });

      const req = httpMock.expectOne(
        `${configService.apiBaseUrl}/agregadas?tipo=BASURA`
      );
      req.flush(mockAgregadas);
    });

    it('debería obtener agregadas con múltiples filtros', (done) => {
      service.obtenerAgregadas('BASURA', 'ALTA').subscribe((data) => {
        expect(data).toEqual(mockAgregadas);
        done();
      });

      const req = httpMock.expectOne(
        `${configService.apiBaseUrl}/agregadas?tipo=BASURA&prioridad=ALTA`
      );
      req.flush(mockAgregadas);
    });
  });

  describe('obtenerEstadisticasPorTipo', () => {
    it('debería obtener estadísticas por tipo', (done) => {
      service.obtenerEstadisticasPorTipo().subscribe((data) => {
        expect(data).toEqual(mockEstadisticas);
        expect(data.length).toBe(1);
        done();
      });

      const req = httpMock.expectOne(
        `${configService.apiBaseUrl}/estadisticas-por-tipo`
      );
      req.flush(mockEstadisticas);
    });
  });

  describe('obtenerPendientes', () => {
    it('debería obtener incidencias pendientes', (done) => {
      service.obtenerPendientes().subscribe((data) => {
        expect(data).toEqual(mockPendientes);
        done();
      });

      const req = httpMock.expectOne(
        `${configService.apiBaseUrl}/pendientes`
      );
      req.flush(mockPendientes);
    });

    it('no debería cachear pendientes', (done) => {
      service.obtenerPendientes().subscribe(() => {
        // Segunda llamada
        service.obtenerPendientes().subscribe(() => {
          done();
        });
      });

      const req1 = httpMock.expectOne(
        `${configService.apiBaseUrl}/pendientes`
      );
      req1.flush(mockPendientes);

      const req2 = httpMock.expectOne(
        `${configService.apiBaseUrl}/pendientes`
      );
      req2.flush(mockPendientes);
    });
  });

  describe('clearCache', () => {
    it('debería limpiar el caché completo', (done) => {
      service.obtenerResumen().subscribe(() => {
        service.clearCache();

        // Después de limpiar, debe hacer una nueva solicitud HTTP
        service.obtenerResumen().subscribe(() => {
          done();
        });

        const req = httpMock.expectOne(
          `${configService.apiBaseUrl}/resumen`
        );
        req.flush(mockResumen);
      });

      const req = httpMock.expectOne(
        `${configService.apiBaseUrl}/resumen`
      );
      req.flush(mockResumen);
    });
  });

  describe('manejo de errores', () => {
    it('debería manejar error 404', (done) => {
      service.obtenerResumen().subscribe({
        next: () => fail('debería haber fallado'),
        error: (error) => {
          expect(error.message).toContain('no encontrado');
          done();
        }
      });

      const req = httpMock.expectOne(
        `${configService.apiBaseUrl}/resumen`
      );
      req.flush('Not Found', { status: 404, statusText: 'Not Found' });
    });

    it('debería manejar error 500', (done) => {
      service.obtenerResumen().subscribe({
        next: () => fail('debería haber fallado'),
        error: (error) => {
          expect(error.message).toContain('Error del servidor');
          done();
        }
      });

      const req = httpMock.expectOne(
        `${configService.apiBaseUrl}/resumen`
      );
      req.flush('Server Error', { status: 500, statusText: 'Internal Server Error' });
    });
  });
});
