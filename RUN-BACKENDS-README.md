# Guía de uso: Script de control de backends

## Instalación

El script `run-backends.ps1` ya está en la raíz del proyecto.

## Uso

### Iniciar todos los backends:
```powershell
.\run-backends.ps1 start
```

### Detener todos los backends:
```powershell
.\run-backends.ps1 stop
```

## Qué hace

- **Start**: Inicia todos los 4 backends (registro-incidencias, priorizacion-incidencias, metricas, notificaciones) usando `mvn spring-boot:run` en background
- **Stop**: Detiene todos los procesos en background

## Detalles

- Cada backend se ejecuta en una ventana minimizada del CMD
- Los logs se guardan en la carpeta `logs/` de la raíz
- Los PIDs se guardan en `.backends-pids.json` para tracking
- URLs disponibles después de iniciar:
  - Registro Incidencias: http://localhost:8080
  - Priorización Incidencias: http://localhost:8082
  - Métricas: http://localhost:8083
  - Notificaciones: http://localhost:8084

## Nota

Si tienes problemas de permisos en PowerShell, ejecuta:
```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```
