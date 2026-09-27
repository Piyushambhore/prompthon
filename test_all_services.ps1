# JanSaarthi Microservices Diagnostic Suite
Write-Host "Checking JanSaarthi Microservices..." -ForegroundColor Cyan

$results = @()

# 1. Scheme Service (:8080)
Write-Host "Testing Scheme Service (:8080)..." -ForegroundColor Yellow
try {
    $schemeOut = powershell -ExecutionPolicy Bypass -File .\test_suite.ps1
    $schemePassed = ($schemeOut -join "`n") -match "ALL 23 / 23 TESTS PASSED"
    $status = if ($schemePassed) { "PASS (23/23)" } else { "FAIL" }
    $results += [PSCustomObject]@{ Service = "scheme-service"; Port = 8080; Status = $status }
    Write-Host "Result: $status" -ForegroundColor Green
} catch {
    $results += [PSCustomObject]@{ Service = "scheme-service"; Port = 8080; Status = "ERROR" }
}

# 2. Eligibility Analyzer (:5001)
Write-Host "Testing Eligibility Analyzer (:5001)..." -ForegroundColor Yellow
Push-Location "backend\eligibility-analyzer"
try {
    $out = npm test 2>&1
    $passed = ($out -join "`n") -match "ALL TEST CASES PASSED"
    $status = if ($passed) { "PASS (6/6)" } else { "FAIL" }
    $results += [PSCustomObject]@{ Service = "eligibility-analyzer"; Port = 5001; Status = $status }
    Write-Host "Result: $status" -ForegroundColor Green
} finally {
    Pop-Location
}

# 3. Language Service (:5002)
Write-Host "Testing Language Service (:5002)..." -ForegroundColor Yellow
Push-Location "backend\language-service"
try {
    $out = npm test 2>&1
    $passed = ($out -join "`n") -match "ALL LANGUAGE SERVICE TESTS PASSED"
    $status = if ($passed) { "PASS (6/6)" } else { "FAIL" }
    $results += [PSCustomObject]@{ Service = "language-service"; Port = 5002; Status = $status }
    Write-Host "Result: $status" -ForegroundColor Green
} finally {
    Pop-Location
}

# 4. Document Generator (:5003)
Write-Host "Testing Document Generator (:5003)..." -ForegroundColor Yellow
Push-Location "backend\document-generator"
try {
    $out = npm test 2>&1
    $passed = ($out -join "`n") -match "ALL DOCUMENT GENERATOR TESTS PASSED"
    $status = if ($passed) { "PASS (8/8)" } else { "FAIL" }
    $results += [PSCustomObject]@{ Service = "document-generator"; Port = 5003; Status = $status }
    Write-Host "Result: $status" -ForegroundColor Green
} finally {
    Pop-Location
}

# 5. Scheme Ingestion Pipeline (:5004)
Write-Host "Testing Scheme Ingestion Pipeline (:5004)..." -ForegroundColor Yellow
Push-Location "backend\scheme-ingestion"
try {
    $out = npm test 2>&1
    $passed = ($out -join "`n") -match "ALL INGESTION PIPELINE TESTS PASSED"
    $status = if ($passed) { "PASS (6/6)" } else { "FAIL" }
    $results += [PSCustomObject]@{ Service = "scheme-ingestion"; Port = 5004; Status = $status }
    Write-Host "Result: $status" -ForegroundColor Green
} finally {
    Pop-Location
}

Write-Host "`nDIAGNOSTIC SUMMARY:" -ForegroundColor Cyan
$results | Format-Table -AutoSize
