# Script para iniciar/parar todos los backends del proyecto urban-events
# Uso: .\run-backends.ps1 start|stop

param(
    [ValidateSet("start", "stop")]
    [string]$Action = "start"
)

$projectRoot = $PSScriptRoot
$pidFile = Join-Path $projectRoot ".backends-pids.json"
$logDir = Join-Path $projectRoot "logs"
$mvnHome = "c:\programas\apache-maven-3.9.9"
$mvnCmd = Join-Path $mvnHome "bin\mvn.cmd"

if (-not (Test-Path $logDir)) {
    New-Item -ItemType Directory -Path $logDir | Out-Null
}

$backends = @(
    @{ name = "registro-incidencias"; port = 8081 },
    @{ name = "priorizacion-incidencias"; port = 8082 },
    @{ name = "notificaciones-quarkus"; port = 8083 },
    @{ name = "metricas-quarkus"; port = 8084 }
)

function Get-ProcessByPort {
    param([int]$Port)
    
    try {
        $netstat = netstat -ano | findstr ":$Port"
        if ($netstat) {
            $parts = $netstat -split '\s+' | Where-Object { $_ -ne '' }
            $pid = $parts[-1]
            return [int]$pid
        }
    } catch {
        return $null
    }
    return $null
}

function Start-Backends {
    Write-Host "[*] Iniciando todos los backends..." -ForegroundColor Green
    $runningProcesses = @()
    
    foreach ($backend in $backends) {
        $name = $backend.name
        $port = $backend.port
        $modulePath = Join-Path $projectRoot $name
        $logFile = Join-Path $logDir "$name.log"
        $errorFile = Join-Path $logDir "$name-error.log"
        
        if (-not (Test-Path $modulePath)) {
            Write-Host "[X] Modulo no encontrado: $modulePath" -ForegroundColor Red
            continue
        }
        
        Write-Host "[+] Iniciando $name (puerto $port)..." -ForegroundColor Cyan
        
        $command = if ($name -like "*quarkus") { "quarkus:dev" } else { "spring-boot:run" }
        
        $scriptBlock = @"
cd /d "$modulePath"
"$mvnCmd" $command
"@
        
        $tempBatch = Join-Path $logDir "start-$name.bat"
        Set-Content -Path $tempBatch -Value $scriptBlock -Encoding ASCII
        
        $process = Start-Process `
            -FilePath "cmd.exe" `
            -ArgumentList "/c $tempBatch" `
            -RedirectStandardOutput $logFile `
            -RedirectStandardError $errorFile `
            -WindowStyle Minimized `
            -PassThru
        
        $runningProcesses += @{
            name = $name
            port = $port
            cmdPid = $process.Id
        }
        
        Write-Host "[OK] $name iniciado (PID CMD: $($process.Id))" -ForegroundColor Green
        Start-Sleep -Seconds 3
    }
    
    $runningProcesses | ConvertTo-Json | Set-Content $pidFile
    
    Write-Host ""
    Write-Host "===========================================================" -ForegroundColor Yellow
    Write-Host "[OK] TODOS LOS BACKENDS INICIADOS" -ForegroundColor Green
    Write-Host "===========================================================" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "URLs disponibles:" -ForegroundColor Cyan
    foreach ($backend in $runningProcesses) {
        Write-Host "  * $($backend.name): http://localhost:$($backend.port)" -ForegroundColor White
    }
    Write-Host ""
    Write-Host "Logs en: $logDir" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Para detener: .\run-backends.ps1 stop" -ForegroundColor Yellow
    Write-Host "===========================================================" -ForegroundColor Yellow
}

function Stop-Backends {
    Write-Host "[*] Deteniendo todos los backends..." -ForegroundColor Yellow
    
    $localPidFile = Join-Path $projectRoot ".backends-pids.json"
    
    # Primero obtener los PIDs de los cmd.exe que están ejecutando los scripts
    $cmdPids = @()
    if (Test-Path $localPidFile) {
        try {
            $processes = Get-Content $localPidFile | ConvertFrom-Json
            if ($processes -is [array]) {
                $cmdPids = $processes | Select-Object -ExpandProperty cmdPid
            } else {
                $cmdPids = @($processes.cmdPid)
            }
        } catch {
            Write-Host "[!] No se pudo leer $localPidFile" -ForegroundColor Yellow
        }
    }
    
    # Matar los procesos cmd.exe primero
    if ($cmdPids.Count -gt 0) {
        Write-Host "[*] Deteniendo procesos CMD..." -ForegroundColor Cyan
        foreach ($cmdPid in $cmdPids) {
            try {
                Stop-Process -Id $cmdPid -Force -ErrorAction SilentlyContinue
                Write-Host "  [OK] CMD PID $cmdPid detenido" -ForegroundColor Green
            } catch {
                Write-Host "  [!] No se pudo detener CMD PID $cmdPid" -ForegroundColor Yellow
            }
        }
    }
    
    # Luego matar java.exe con taskkill (más agresivo)
    Write-Host "[*] Deteniendo procesos Java..." -ForegroundColor Cyan
    $result = cmd /c "taskkill /IM java.exe /F 2>&1"
    Write-Host $result -ForegroundColor Yellow
    
    Write-Host "[*] Esperando 3 segundos..." -ForegroundColor Yellow
    Start-Sleep -Seconds 3
    
    $remaining = @(Get-Process java -ErrorAction SilentlyContinue)
    if ($remaining.Count -eq 0) {
        Write-Host "[OK] Todos los procesos Java detenidos" -ForegroundColor Green
    } else {
        Write-Host "[!] Aun hay $($remaining.Count) procesos Java" -ForegroundColor Yellow
    }
    
    # Limpiar archivo de PIDs si existe
    if (Test-Path $localPidFile) {
        Remove-Item $localPidFile -Force -ErrorAction SilentlyContinue
    }
    
    # Limpiar logs temp
    Get-Item "$logDir/start-*.bat" -ErrorAction SilentlyContinue | Remove-Item -Force
    
    Write-Host ""
    Write-Host "===========================================================" -ForegroundColor Yellow
    Write-Host "[OK] STOP COMPLETADO" -ForegroundColor Green  
    Write-Host "===========================================================" -ForegroundColor Yellow
}

switch ($Action) {
    "start" { Start-Backends }
    "stop" { Stop-Backends }
}
