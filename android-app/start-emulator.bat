@echo off
setlocal enabledelayedexpansion

:: ============================================================
::  start-emulator.bat
::  Starts adb + the SmartRoad Android emulator and waits until
::  Android has fully booted AND the system is actually healthy
::  (not just "sys.boot_completed=1" -- see health check below).
::
::  Override the AVD used by setting AVD_NAME before calling, e.g.
::      set AVD_NAME=Pixel_8_API_35
::      start-emulator.bat
:: ============================================================

if "%AVD_NAME%"=="" set "AVD_NAME=Medium_Phone_API_36.1"

:: --- Locate the Android SDK automatically ---
if not "%ANDROID_HOME%"=="" (
    set "SDK=%ANDROID_HOME%"
) else if not "%ANDROID_SDK_ROOT%"=="" (
    set "SDK=%ANDROID_SDK_ROOT%"
) else if exist "%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" (
    set "SDK=%LOCALAPPDATA%\Android\Sdk"
) else (
    echo [ERROR] Could not find the Android SDK.
    echo         Set ANDROID_HOME to your SDK path and try again.
    exit /b 1
)

set "ADB=%SDK%\platform-tools\adb.exe"
set "EMULATOR=%SDK%\emulator\emulator.exe"

if not exist "%ADB%" (
    echo [ERROR] adb.exe not found at "%ADB%"
    exit /b 1
)
if not exist "%EMULATOR%" (
    echo [ERROR] emulator.exe not found at "%EMULATOR%"
    exit /b 1
)

echo Using SDK:      %SDK%
echo Using AVD:       %AVD_NAME%

echo.
echo [1/5] Starting adb server...
"%ADB%" start-server

:: --- Confirm the requested AVD actually exists ---
"%EMULATOR%" -list-avds | findstr /i /c:"%AVD_NAME%" >nul
if not %errorlevel%==0 (
    echo [ERROR] AVD "%AVD_NAME%" was not found. Available AVDs:
    "%EMULATOR%" -list-avds
    exit /b 1
)

set "RETRY_WITH_COLD_BOOT=0"
set "BOOT_ATTEMPT=0"

:launch_emulator
set /a BOOT_ATTEMPT+=1

:: --- Skip launching a new instance if one is already online ---
"%ADB%" devices | findstr /r "^emulator-" | findstr /v /c:"offline" | findstr /c:"device" >nul
if %errorlevel%==0 (
    echo [INFO] An emulator is already attached and online. Skipping launch.
    goto :wait_boot
)

echo.
echo [2/5] Launching emulator "%AVD_NAME%" in the background (attempt %BOOT_ATTEMPT%)...
if "%RETRY_WITH_COLD_BOOT%"=="1" (
    echo        Previous boot looked unhealthy - forcing a clean cold boot this time.
    start "SmartRoad Emulator" "%EMULATOR%" -avd %AVD_NAME% -no-snapshot-load -netdelay none -netspeed full
) else (
    start "SmartRoad Emulator" "%EMULATOR%" -avd %AVD_NAME% -netdelay none -netspeed full
)

echo.
echo [3/5] Waiting for the device to attach to adb...
set "ATTACH_TRIES=0"
:attach_wait_loop
"%ADB%" devices | findstr /r "^emulator-" >nul
if %errorlevel%==0 goto :wait_boot
set /a ATTACH_TRIES+=1
if %ATTACH_TRIES% GEQ 60 (
    echo [ERROR] Emulator never attached to adb after 3 minutes.
    echo         Try starting Android Studio's Device Manager and check for errors.
    exit /b 1
)
timeout /t 3 /nobreak >nul
goto :attach_wait_loop

:wait_boot
echo.
echo [4/5] Waiting for Android to finish booting (this can take a while on first/cold boot)...
set "BOOT_TRIES=0"
:boot_wait_loop
for /f %%i in ('"%ADB%" shell getprop sys.boot_completed 2^>nul') do set "BOOT_COMPLETE=%%i"
if "%BOOT_COMPLETE%"=="1" goto :health_check
set /a BOOT_TRIES+=1
if %BOOT_TRIES% GEQ 100 (
    echo [ERROR] Emulator did not finish booting after 5 minutes.
    exit /b 1
)
timeout /t 3 /nobreak >nul
goto :boot_wait_loop

:health_check
echo.
echo [5/5] Verifying the system is actually healthy (package manager responding)...
:: Known failure mode: booted snapshot restores with a crashed system_server.
:: sys.boot_completed still reports 1, but every pm/am call fails with
:: "Failure calling service ...: Broken pipe". Detect and recover by
:: force-killing the emulator and cold-booting once.
"%ADB%" shell pm list packages > "%TEMP%\smartroad_health.log" 2>&1
findstr /c:"Failure calling service" "%TEMP%\smartroad_health.log" >nul
if %errorlevel%==0 (
    if "%RETRY_WITH_COLD_BOOT%"=="1" (
        echo [ERROR] System server is still unhealthy after a cold boot retry.
        echo         Manual intervention required - try wiping the AVD data from
        echo         Android Studio's Device Manager ^(the three-dot menu ^> Wipe Data^).
        exit /b 1
    )
    echo [FIX] Detected a dead system_server ^(broken pipe on package manager^).
    echo       Force-killing the emulator and cold-booting fresh...
    "%ADB%" emu kill >nul 2>&1
    taskkill /F /IM emulator.exe >nul 2>&1
    taskkill /F /IM qemu-system-x86_64.exe >nul 2>&1
    "%ADB%" kill-server
    timeout /t 3 /nobreak >nul
    "%ADB%" start-server
    set "RETRY_WITH_COLD_BOOT=1"
    goto :launch_emulator
)

echo.
echo Emulator is up, fully booted, and healthy.
"%ADB%" devices -l
endlocal
