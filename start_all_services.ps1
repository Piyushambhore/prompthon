# JanSaarthi (GSGP) — Microservices Launcher
# Starts all 5 backend microservices in separate background processes

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "  Launching JanSaarthi (GSGP) Microservices Suite" -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

$root = $PSScriptRoot

# 1. Scheme Service (:8080)
Write-Host "[1/5] Starting Scheme Service (Kotlin/Ktor on :8080)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root\backend\scheme-service'; `$env:JAVA_HOME = 'C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot'; `$env:PATH = '`$env:JAVA_HOME\bin;' + `$env:PATH; .\gradlew.bat run"

# 2. Eligibility Analyzer (:5001)
Write-Host "[2/5] Starting Eligibility Analyzer (Node.js on :5001)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root\backend\eligibility-analyzer'; npm run dev"

# 3. Language Service (:5002)
Write-Host "[3/5] Starting Language Service (Node.js on :5002)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root\backend\language-service'; npm run dev"

# 4. Document Generator (:5003)
Write-Host "[4/5] Starting Document Generator (Node.js on :5003)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root\backend\document-generator'; npm run dev"

# 5. Scheme Ingestion Pipeline (:5004)
Write-Host "[5/5] Starting Scheme Ingestion Pipeline (Node.js on :5004)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root\backend\scheme-ingestion'; npm run dev"

Write-Host "`nAll 5 microservices have been launched in separate terminal windows!" -ForegroundColor Green
Write-Host "To verify health, run: .\test_all_services.ps1" -ForegroundColor Cyan
