@echo off
setlocal enabledelayedexpansion

:: ============================================================
::  start_emulator.bat
::  Universal Android emulator launcher.
::   - Auto-detects the Android SDK (ANDROID_HOME / ANDROID_SDK_ROOT
::     / common install paths).
::   - Auto-detects installed AVDs via "emulator -list-avds"
::     (never hardcoded).
::   - If multiple AVDs exist, shows a numbered menu.
::   - If exactly one exists, launches it automatically.
::   - Waits for full boot, unlocks the screen, and verifies the
::     device is healthy and visible in "adb devices".
::
::  Override AVD selection non-interactively:
::      set AVD_NAME=Pixel_8_API_35
::      start_emulator.bat
:: ============================================================

set "SDK="
set "AVD_NAME_OVERRIDE=%AVD_NAME%"

:: --- [1/8] Locate the Android SDK automatically ---
echo [1/8] Locating Android SDK...

if not "%ANDROID_HOME%"=="" if exist "%ANDROID_HOME%\platform-tools\adb.exe" set "SDK=%ANDROID_HOME%"
if "%SDK%"=="" if not "%ANDROID_SDK_ROOT%"=="" if exist "%ANDROID_SDK_ROOT%\platform-tools\adb.exe" set "SDK=%ANDROID_SDK_ROOT%"
if "%SDK%"=="" if exist "%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" set "SDK=%LOCALAPPDATA%\Android\Sdk"
if "%SDK%"=="" if exist "C:\Android\Sdk\platform-tools\adb.exe" set "SDK=C:\Android\Sdk"
if "%SDK%"=="" if exist "C:\Program Files (x86)\Android\android-sdk\platform-tools\adb.exe" set "SDK=C:\Program Files (x86)\Android\android-sdk"
if "%SDK%"=="" if exist "C:\Program Files\Android\android-sdk\platform-tools\adb.exe" set "SDK=C:\Program Files\Android\android-sdk"

if "%SDK%"=="" (
    echo [ERROR] Could not find the Android SDK.
    echo         Set ANDROID_HOME to your SDK path and try again, e.g.:
    echo             set ANDROID_HOME=C:\Users\%USERNAME%\AppData\Local\Android\Sdk
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
    echo         Install the "Android Emulator" package via Android Studio's SDK Manager.
    exit /b 1
)

echo        SDK found: %SDK%

:: --- [2/8] Start adb server if it isn't already running ---
echo.
echo [2/8] Starting adb server...
"%ADB%" start-server >nul 2>&1

:: --- [3/8] Detect installed AVDs ---
echo.
echo [3/8] Detecting installed emulators (AVDs)...
set "AVD_COUNT=0"
for /f "usebackq delims=" %%A in (`"%EMULATOR%" -list-avds`) do (
    if not "%%A"=="" (
        set /a AVD_COUNT+=1
        set "AVD_!AVD_COUNT!=%%A"
    )
)

if %AVD_COUNT%==0 (
    echo [ERROR] No Android Virtual Devices ^(AVDs^) found.
    echo         Open Android Studio's Device Manager and create one, then try again.
    exit /b 1
)

if not "%AVD_NAME_OVERRIDE%"=="" (
    set "AVD_NAME=%AVD_NAME_OVERRIDE%"
    echo        Using AVD from AVD_NAME environment variable: !AVD_NAME!
) else if %AVD_COUNT%==1 (
    set "AVD_NAME=!AVD_1!"
    echo        One AVD found - launching automatically: !AVD_NAME!
) else (
    echo        Multiple AVDs found:
    echo.
    for /l %%i in (1,1,%AVD_COUNT%) do echo        %%i^) !AVD_%%i!
    echo.
    set "CHOICE="
    set /p "CHOICE=Select an emulator [1-%AVD_COUNT%]: "
    if "!CHOICE!"=="" (
        echo [ERROR] No selection made.
        exit /b 1
    )
    for /f "delims=0123456789" %%x in ("!CHOICE!") do (
        echo [ERROR] "!CHOICE!" is not a valid number.
        exit /b 1
    )
    if !CHOICE! LSS 1 (
        echo [ERROR] "!CHOICE!" is out of range.
        exit /b 1
    )
    if !CHOICE! GTR %AVD_COUNT% (
        echo [ERROR] "!CHOICE!" is out of range.
        exit /b 1
    )
    set "AVD_NAME=!AVD_%CHOICE%!"
    echo        Selected: !AVD_NAME!
)

set "RETRY_WITH_COLD_BOOT=0"
set "BOOT_ATTEMPT=0"

:launch_emulator
set /a BOOT_ATTEMPT+=1

:: --- Skip launching a new instance if one is already online ---
"%ADB%" devices | findstr /r "^emulator-" | findstr /v /c:"offline" | findstr /c:"device" >nul
if %errorlevel%==0 (
    echo.
    echo [INFO] An emulator is already attached and online. Skipping launch.
    goto :wait_boot
)

echo.
echo [4/8] Launching emulator "!AVD_NAME!" in the background ^(attempt %BOOT_ATTEMPT%^)...
if "%RETRY_WITH_COLD_BOOT%"=="1" (
    echo        Previous boot looked unhealthy - forcing a clean cold boot this time.
    start "Android Emulator - !AVD_NAME!" "%EMULATOR%" -avd "!AVD_NAME!" -no-snapshot-load -netdelay none -netspeed full
) else (
    start "Android Emulator - !AVD_NAME!" "%EMULATOR%" -avd "!AVD_NAME!" -netdelay none -netspeed full
)

echo.
echo [5/8] Waiting for the device to attach to adb...
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
echo [6/8] Waiting for Android to finish booting ^(can take a while on first/cold boot^)...
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
echo [7/8] Verifying the system is healthy ^(package manager responding^)...
:: Known failure mode: a restored snapshot can leave system_server crashed
:: even though sys.boot_completed reports 1 - every pm/am call then fails
:: with "Failure calling service ...: Broken pipe". Detect and recover by
:: force-killing the emulator and cold-booting once.
"%ADB%" shell pm list packages > "%TEMP%\android_launcher_health.log" 2>&1
findstr /c:"Failure calling service" "%TEMP%\android_launcher_health.log" >nul
if %errorlevel%==0 (
    if "%RETRY_WITH_COLD_BOOT%"=="1" (
        echo [ERROR] System server is still unhealthy after a cold boot retry.
        echo         Manual intervention required - try wiping the AVD data from
        echo         Android Studio's Device Manager ^(three-dot menu ^> Wipe Data^).
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

:: --- [8/8] Unlock the screen so UI automation / app launch works ---
echo.
echo [8/8] Unlocking the emulator screen...
"%ADB%" shell input keyevent 82 >nul 2>&1
"%ADB%" shell wm dismiss-keyguard >nul 2>&1

echo.
echo Emulator is up, fully booted, unlocked, and healthy.
"%ADB%" devices -l
endlocal
