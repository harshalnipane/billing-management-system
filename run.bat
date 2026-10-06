@echo off
setlocal enabledelayedexpansion

echo =======================================================================
echo   Starting Serene Palms Luxury Villa & Resort Billing Management System
echo   Indian GST Compliant (CGST / SGST / IGST Engine)
echo =======================================================================
echo.

if not exist "bin" mkdir "bin"

echo [+] Compiling Java backend files...
dir /s /b src\main\java\*.java > sources.tmp
javac -encoding UTF-8 -d bin @sources.tmp
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Java compilation failed!
    del sources.tmp
    pause
    exit /b %ERRORLEVEL%
)
del sources.tmp

echo [+] Compilation successful!
echo [+] Starting HTTP REST Application on port 8080...
echo.

java -cp bin com.resort.billing.BillingApplication 8080
pause
