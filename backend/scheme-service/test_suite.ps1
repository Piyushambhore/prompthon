$base = "http://localhost:8080"
$tests = [System.Collections.Generic.List[PSCustomObject]]::new()

function Test-Endpoint {
    param(
        [string]$name,
        [string]$method,
        [string]$url,
        [string]$body,
        [int]$expectedCode
    )
    try {
        $headers = @{ "Content-Type" = "application/json" }
        $params = @{
            Uri = $url
            Method = $method
            Headers = $headers
            UseBasicParsing = $true
        }
        if ($body) {
            $params["Body"] = $body
        }
        $resp = Invoke-WebRequest @params
        $status = $resp.StatusCode
    } catch {
        if ($_.Exception.Response) {
            $status = [int]$_.Exception.Response.StatusCode
        } else {
            $status = 0
        }
    }

    $passed = ($status -eq $expectedCode)
    $obj = [PSCustomObject]@{
        Test = $name
        Method = $method.ToUpper()
        Endpoint = $url.Replace($base, "")
        Status = $status
        Expected = $expectedCode
        Result = if ($passed) { "PASS" } else { "FAIL" }
    }
    $tests.Add($obj)
}

Write-Host "Running JanSaarthi 100% Comprehensive Endpoint Test Suite..." -ForegroundColor Cyan

# 1. Health
Test-Endpoint "1. Health Check" "Get" "$base/health" $null 200

# 2. Scheme Discovery & State Portals
Test-Endpoint "2. All Schemes" "Get" "$base/api/schemes" $null 200
Test-Endpoint "3. Filter Schemes by State" "Get" "$base/api/schemes?state=Maharashtra" $null 200
Test-Endpoint "4. Filter Schemes by Category" "Get" "$base/api/schemes?category=agriculture" $null 200
Test-Endpoint "5. State Portals (All 17)" "Get" "$base/api/schemes/state-portals" $null 200
Test-Endpoint "6. State Portals (Maharashtra)" "Get" "$base/api/schemes/state-portals?state=Maharashtra" $null 200

# 3. Scheme Details
Test-Endpoint "5. Scheme Details (SCH005)" "Get" "$base/api/schemes/SCH005" $null 200
Test-Endpoint "6. Scheme Details (Nonexistent 404)" "Get" "$base/api/schemes/NONEXISTENT" $null 404

# 4. Eligibility Engine
$eligBody = '{"age":22,"state":"Maharashtra","occupation":"farmer","annualIncome":120000,"category":"OBC","student":false}'
Test-Endpoint "7. Check Eligibility (Valid)" "Post" "$base/api/schemes/check-eligibility" $eligBody 200

$badEligBody = '{"age":-5,"state":"","occupation":"","annualIncome":-10,"category":"INVALID"}'
Test-Endpoint "8. Check Eligibility (Validation 400)" "Post" "$base/api/schemes/check-eligibility" $badEligBody 400

# 5. Document Readiness
$docSingleBody = '{"schemeId":"SCH005","availableDocuments":["Aadhaar Card","Land Ownership Records"]}'
Test-Endpoint "9. Document Readiness (Single)" "Post" "$base/api/documents/check-readiness" $docSingleBody 200

$docBulkBody = '{"schemeIds":["SCH005","SCH006","SCH007"],"availableDocuments":["Aadhaar Card","Bank Passbook"]}'
Test-Endpoint "10. Document Readiness (Bulk)" "Post" "$base/api/documents/check-readiness/bulk" $docBulkBody 200

Test-Endpoint "11. Required Documents List" "Get" "$base/api/documents/required/SCH005" $null 200

# 6. Notice Explainer
$explainBody = '{"text":"Your application for post matric scholarship has been rejected due to invalid income certificate submitted beyond 30 days deadline."}'
Test-Endpoint "12. Notice Explainer" "Post" "$base/api/explain" $explainBody 200

# 7. DigiLocker OAuth & Documents
Test-Endpoint "13. DigiLocker Auth Initiation" "Get" "$base/api/profile/digilocker/auth" $null 200

$cbBody = '{"authorizationCode":"code_test_123","state":"state_xyz"}'
Test-Endpoint "14. DigiLocker Callback" "Post" "$base/api/profile/digilocker/callback" $cbBody 200

Test-Endpoint "15. DigiLocker Documents Fetch" "Get" "$base/api/profile/digilocker/documents?token=dl_token_demo" $null 200
Test-Endpoint "16. DigiLocker Docs Without Token (401)" "Get" "$base/api/profile/digilocker/documents" $null 401

# 8. Farmer Specific Profile
$farmerBody = '{"aadhaarLinked":true,"pmKisanBeneficiary":true,"landOwnership":{"hasLand":true,"landAreaAcres":2.0},"state":"Maharashtra"}'
Test-Endpoint "17. Farmer Profile Enrichment" "Post" "$base/api/profile/farmer" $farmerBody 200

# 9. Unified Profile Builder
$buildManualBody = '{"mode":"manual","age":25,"state":"Maharashtra","occupation":"worker","annualIncome":180000,"category":"General"}'
Test-Endpoint "18. Unified Profile (Manual Mode)" "Post" "$base/api/profile/build" $buildManualBody 200

$buildDigiBody = '{"mode":"digilocker","digiLockerToken":"dl_token_demo"}'
Test-Endpoint "19. Unified Profile (DigiLocker Mode)" "Post" "$base/api/profile/build" $buildDigiBody 200

# 10. End-to-End Profile Eligibility
$checkFarmerBody = '{"mode":"farmer","farmerProfile":{"aadhaarLinked":true,"pmKisanBeneficiary":true,"landOwnership":{"hasLand":true,"landAreaAcres":1.5},"state":"Maharashtra"}}'
Test-Endpoint "20. End-to-End Eligibility (Farmer)" "Post" "$base/api/profile/check-eligibility" $checkFarmerBody 200

$checkDigiBody = '{"mode":"digilocker","digiLockerToken":"dl_token_demo","occupation":"student","isStudent":true}'
Test-Endpoint "21. End-to-End Eligibility (DigiLocker)" "Post" "$base/api/profile/check-eligibility" $checkDigiBody 200

$tests | Format-Table -AutoSize
$passCount = ($tests | Where-Object { $_.Result -eq "PASS" }).Count
$totalCount = $tests.Count

Write-Host "=========================================" -ForegroundColor Yellow
if ($passCount -eq $totalCount) {
    Write-Host ">>> ALL $totalCount / $totalCount TESTS PASSED (100% OPERATIONAL) <<<" -ForegroundColor Green
} else {
    Write-Host ">>> $passCount / $totalCount PASSED ($($totalCount - $passCount) FAILED) <<<" -ForegroundColor Red
}
Write-Host "=========================================" -ForegroundColor Yellow
