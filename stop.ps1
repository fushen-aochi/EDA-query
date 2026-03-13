$ErrorActionPreference = "Stop"

$rootDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$runDir = Join-Path $rootDir ".run"

function Stop-ProcessByPidFile {
    param(
        [string]$Name,
        [string]$PidFile
    )

    if (-not (Test-Path $PidFile)) {
        Write-Host "No PID file for ${Name}: $PidFile"
        return $false
    }

    $rawPid = Get-Content $PidFile -ErrorAction SilentlyContinue
    if (-not $rawPid) {
        Write-Host "Empty PID file for ${Name}: $PidFile"
        Remove-Item $PidFile -ErrorAction SilentlyContinue
        return $false
    }

    $pidValue = 0
    if (-not [int]::TryParse($rawPid.Trim(), [ref]$pidValue)) {
        Write-Host "Invalid PID in $PidFile"
        Remove-Item $PidFile -ErrorAction SilentlyContinue
        return $false
    }

    $proc = Get-Process -Id $pidValue -ErrorAction SilentlyContinue
    if ($null -eq $proc) {
        Write-Host "$Name process not running (PID=$pidValue)."
        Remove-Item $PidFile -ErrorAction SilentlyContinue
        return $false
    }

    Stop-Process -Id $pidValue -Force
    Write-Host "Stopped $Name (PID=$pidValue)."
    Remove-Item $PidFile -ErrorAction SilentlyContinue
    return $true
}

function Stop-ProcessByPort {
    param(
        [string]$Name,
        [int]$Port
    )

    $conn = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($null -eq $conn) {
        Write-Host "$Name port $Port is not listening."
        return $false
    }

    $pidValue = $conn.OwningProcess
    if ($null -eq $pidValue -or $pidValue -le 0) {
        Write-Host "$Name port $Port has no valid process ID."
        return $false
    }

    $proc = Get-Process -Id $pidValue -ErrorAction SilentlyContinue
    if ($null -eq $proc) {
        Write-Host "$Name process not found for port $Port (PID=$pidValue)."
        return $false
    }

    Stop-Process -Id $pidValue -Force
    Write-Host "Stopped $Name via port $Port (PID=$pidValue)."
    return $true
}

if (-not (Test-Path $runDir)) {
    New-Item -ItemType Directory -Path $runDir | Out-Null
}

Write-Host "[1/2] Stopping backend..."
$backendStopped = Stop-ProcessByPidFile -Name "backend" -PidFile (Join-Path $runDir "backend.pid")
if (-not $backendStopped) {
    Stop-ProcessByPort -Name "backend" -Port 8080 | Out-Null
}

Write-Host "[2/2] Stopping frontend..."
$frontendStopped = Stop-ProcessByPidFile -Name "frontend" -PidFile (Join-Path $runDir "frontend.pid")
if (-not $frontendStopped) {
    Stop-ProcessByPort -Name "frontend" -Port 5173 | Out-Null
}

Write-Host "Done."
