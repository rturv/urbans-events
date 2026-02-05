import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ChartConfiguration, ChartData } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';

@Component({
  selector: 'app-root',
  imports: [CommonModule, BaseChartDirective],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App implements OnInit {
  status: 'loading' | 'ready' | 'error' = 'loading';
  lastUpdated: string | null = null;
  notificacionesTotales = 0;

  tipoChartData: ChartData<'bar'> = { labels: [], datasets: [] };
  prioridadChartData: ChartData<'doughnut'> = { labels: [], datasets: [] };
  evolucionChartData: ChartData<'line'> = { labels: [], datasets: [] };

  barOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: false }
    },
    scales: {
      x: { grid: { display: false } },
      y: { beginAtZero: true }
    }
  };

  doughnutOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { position: 'bottom' }
    }
  };

  lineOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: false }
    },
    scales: {
      x: { grid: { display: false } },
      y: { beginAtZero: true }
    }
  };

  private readonly metricasUrl = 'http://localhost:8084/metricas';

  constructor(private readonly http: HttpClient) {}

  ngOnInit(): void {
    this.cargarMetricas();
  }

  cargarMetricas(): void {
    this.status = 'loading';
    this.http.get<ResumenResponse>(this.metricasUrl).subscribe({
      next: (data) => {
        this.actualizarGraficas(data);
        this.status = 'ready';
        this.lastUpdated = new Date().toLocaleString();
      },
      error: () => {
        this.status = 'error';
      }
    });
  }

  private actualizarGraficas(data: ResumenResponse): void {
    const tipos = Object.keys(data.incidenciasPorTipo || {});
    const tipoValores = tipos.map((tipo) => data.incidenciasPorTipo[tipo]);

    this.tipoChartData = {
      labels: tipos,
      datasets: [
        {
          label: 'Incidencias por tipo',
          data: tipoValores,
          backgroundColor: ['#0e6f3b', '#2b9155', '#3ca06a', '#4db182', '#69c09a']
        }
      ]
    };

    const prioridades = Object.keys(data.prioridades || {});
    const prioridadValores = prioridades.map((key) => data.prioridades[key]);
    this.prioridadChartData = {
      labels: prioridades,
      datasets: [
        {
          label: 'Distribucion por prioridad',
          data: prioridadValores,
          backgroundColor: ['#0e6f3b', '#f0b429', '#d6453d']
        }
      ]
    };

    const total = tipoValores.reduce((sum, value) => sum + value, 0);
    const serie = this.crearSerieTemporal(total);
    this.evolucionChartData = {
      labels: serie.labels,
      datasets: [
        {
          label: 'Evolucion semanal',
          data: serie.data,
          borderColor: '#0e6f3b',
          backgroundColor: 'rgba(14, 111, 59, 0.15)',
          fill: true,
          tension: 0.35
        }
      ]
    };

    this.notificacionesTotales = data.notificacionesTotales || 0;
  }

  private crearSerieTemporal(total: number): { labels: string[]; data: number[] } {
    const labels = ['Lun', 'Mar', 'Mie', 'Jue', 'Vie', 'Sab', 'Dom'];
    const base = Math.max(1, Math.round(total / labels.length));
    const data = labels.map((_, index) => Math.max(0, base + (index % 2 === 0 ? 1 : -1)));
    return { labels, data };
  }
}

interface ResumenResponse {
  incidenciasPorTipo: Record<string, number>;
  prioridades: Record<string, number>;
  notificacionesTotales: number;
}
