# Serene Palms Villa & Resort Billing System - PowerShell Runner

Write-Host "=======================================================================" -ForegroundColor Cyan
Write-Host "  Serene Palms Luxury Villa & Resort Billing Management System" -ForegroundColor Yellow
Write-Host "  Indian GST Compliant Engine (CGST / SGST / IGST)" -ForegroundColor Green
Write-Host "=======================================================================" -ForegroundColor Cyan

if (!(Test-Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

Write-Host "[+] Compiling Java source code..." -ForegroundColor Gray
$javaFiles = (Get-ChildItem -Recurse -Filter *.java src\main\java).FullName
javac -encoding UTF-8 -d bin $javaFiles

if ($LASTEXITCODE -eq 0) {
    Write-Host "[+] Compilation successful!" -ForegroundColor Green
    Write-Host "[+] Starting application server at http://localhost:8080 ..." -ForegroundColor Cyan
    java -cp bin com.resort.billing.BillingApplication 8080
} else {
    Write-Host "[!] Compilation failed with exit code $LASTEXITCODE" -ForegroundColor Red
}
