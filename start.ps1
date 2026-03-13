$ErrorActionPreference = "Stop"

$rootDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$backendDir = Join-Path $rootDir "backend"
$frontendDir = Join-Path $rootDir "frontend"
$logsDir = Join-Path $rootDir ".run"
$backendJar = Join-Path $backendDir "target\assistant-backend-0.0.1-SNAPSHOT.jar"

if (-not (Test-Path $logsDir)) {
    New-Item -ItemType Directory -Path $logsDir | Out-Null
}

function Test-PortInUse {
    param([int]$Port)
    $conn = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue
    return $null -ne $conn
}

Write-Host "[1/4] Checking backend package..."
if (-not (Test-Path $backendJar)) {
    Write-Host "Backend jar not found, building with Maven..."
    Push-Location $backendDir
    try {
        mvn clean package -DskipTests
    }
    finally {
        Pop-Location
    }
}

Write-Host "[2/4] Checking frontend dependencies..."
$nodeModules = Join-Path $frontendDir "node_modules"
if (-not (Test-Path $nodeModules)) {
    Push-Location $frontendDir
    try {
        npm install
    }
    finally {
        Pop-Location
    }
}

Write-Host "[3/4] Starting backend..."
if (Test-PortInUse -Port 8080) {
    Write-Host "Port 8080 is already in use. Skipping backend start."
}
else {
    $backendOut = Join-Path $logsDir "backend.out.log"
    $backendErr = Join-Path $logsDir "backend.err.log"
    $backendProcess = Start-Process -FilePath "java" -ArgumentList "-jar", $backendJar -WorkingDirectory $backendDir -RedirectStandardOutput $backendOut -RedirectStandardError $backendErr -PassThru
    Set-Content -Path (Join-Path $logsDir "backend.pid") -Value $backendProcess.Id
    Write-Host "Backend started. PID=$($backendProcess.Id)"
}

Write-Host "[4/4] Starting frontend..."
if (Test-PortInUse -Port 5173) {
    Write-Host "Port 5173 is already in use. Skipping frontend start."
}
else {
    $frontendOut = Join-Path $logsDir "frontend.out.log"
    $frontendErr = Join-Path $logsDir "frontend.err.log"
    $frontendProcess = Start-Process -FilePath "npm.cmd" -ArgumentList "run", "dev" -WorkingDirectory $frontendDir -RedirectStandardOutput $frontendOut -RedirectStandardError $frontendErr -PassThru
    Set-Content -Path (Join-Path $logsDir "frontend.pid") -Value $frontendProcess.Id
    Write-Host "Frontend started. PID=$($frontendProcess.Id)"
}

Write-Host "Done."
Write-Host "Frontend: http://localhost:5173"
Write-Host "Backend : http://localhost:8080"
Write-Host "Logs    : $logsDir"
