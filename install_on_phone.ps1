# JanSaarthi (GSGP) - Android Phone Connect & Install Script
# Automatically connects your phone via USB Debugging, sets up ADB reverse port forwarding,
# and installs/launches the JanSaarthi App on your device.

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host " 📱 JanSaarthi - Android Device Installer & Runner        " -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

# 1. Locate ADB
$adbPath = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"
if (-not (Test-Path $adbPath)) {
    $adbCmd = Get-Command adb -ErrorAction SilentlyContinue
    if ($adbCmd) {
        $adbPath = $adbCmd.Source
    } else {
        Write-Host "❌ Error: ADB not found. Please ensure Android SDK Platform Tools are installed." -ForegroundColor Red
        Exit 1
    }
}
Write-Host "✓ ADB detected: $adbPath" -ForegroundColor Green

# 2. Check connected devices
Write-Host "Checking for connected Android devices via USB debugging..." -ForegroundColor Yellow
$devicesOutput = & $adbPath devices
Write-Host $devicesOutput

$deviceLines = $devicesOutput -split "`r?`n" | Where-Object { $_ -match "\bdevice$" }
if ($deviceLines.Count -eq 0) {
    Write-Host "❌ No authorized Android device detected!" -ForegroundColor Red
    Write-Host "👉 Please ensure:" -ForegroundColor Yellow
    Write-Host "   1. USB Cable is firmly connected."
    Write-Host "   2. 'USB Debugging' is enabled in Developer Options."
    Write-Host "   3. Accept the 'Allow USB Debugging' popup prompt on your phone screen."
    Exit 1
}

$deviceCount = $deviceLines.Count
Write-Host "✓ Found $deviceCount connected Android device(s)!" -ForegroundColor Green

# 3. Setup ADB Reverse Port Forwarding for Frontend & All Microservices
Write-Host "`nSetting up reverse port forwarding (Phone -> PC Localhost)..." -ForegroundColor Yellow
& $adbPath reverse tcp:3000 tcp:3000   # Vite Frontend
& $adbPath reverse tcp:8080 tcp:8080   # Ktor Scheme Service
& $adbPath reverse tcp:5001 tcp:5001   # Language Service
& $adbPath reverse tcp:5002 tcp:5002   # Eligibility Analyzer
& $adbPath reverse tcp:5003 tcp:5003   # Document Generator
& $adbPath reverse tcp:5004 tcp:5004   # Scheme Ingestion
Write-Host "✓ Reversed ports 3000, 8080, 5001, 5002, 5003, 5004 successfully!" -ForegroundColor Green

# 4. Check if native APK is built
$apkPath = "$PSScriptRoot\app\build\outputs\apk\debug\app-debug.apk"
if (-not (Test-Path $apkPath)) {
    $apkPath = "$PSScriptRoot\android\app\build\outputs\apk\debug\app-debug.apk"
}
if (Test-Path $apkPath) {
    Write-Host "`n📦 Found compiled APK at: $apkPath" -ForegroundColor Cyan
    Write-Host "Installing JanSaarthi APK on your phone via ADB..." -ForegroundColor Yellow
    & $adbPath install -r -d $apkPath
    Write-Host "✓ APK Installed successfully!" -ForegroundColor Green
    
    Write-Host "Launching JanSaarthi App on your phone..." -ForegroundColor Cyan
    & $adbPath shell am start -n org.jansaarthi.app/org.jansaarthi.app.MainActivity
    Write-Host "🎉 JanSaarthi is now running natively on your phone!" -ForegroundColor Green
} else {
    Write-Host "`n🌐 Opening JanSaarthi in your phone's browser via high-speed USB..." -ForegroundColor Cyan
    & $adbPath shell am start -a android.intent.action.VIEW -d "http://localhost:3000"
    Write-Host "✓ JanSaarthi opened on your phone screen!" -ForegroundColor Green
    Write-Host "💡 Tip: Tap 'Install App' or 'Add to Home Screen' in Chrome for fullscreen app mode!" -ForegroundColor Yellow
}

Write-Host "`n==========================================================" -ForegroundColor Cyan
Write-Host " Everything is connected and live on your Android device! " -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan
