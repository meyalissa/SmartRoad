@echo off
setlocal enabledelayedexpansion

:: ============================================================
::  start_project.bat
::  Builds the SmartRoad debug APK, installs it on whatever
::  device/emulator is currently connected, launches the app,
::  and opens a separate Logcat window for it.
::
::  Requires a device already attached ^(run start_emulator.bat
::  or start_all.bat first if nothing is connected^).
:: ============================================================

set "PROJECT_DIR=%~dp0"
set "PACKAGE=com.smartroad"
set "LAUNCH_ACTIVITY=com.smartroad/.ui.splash.SplashActivity"
set "APK_PATH=%PROJECT_DIR%app\build\outputs\apk\debug\app-debug.apk"
set "GRADLEW=%PROJECT_DIR%gradlew.bat"

:: --- Locate the Android SDK automatically ---
set "SDK="
if not "%ANDROID_HOME%"=="" if exist "%ANDROID_HOME%\platform-tools\adb.exe" set "SDK=%ANDROID_HOME%"
if "%SDK%"=="" if not "%ANDROID_SDK_ROOT%"=="" if exist "%ANDROID_SDK_ROOT%\platform-tools\adb.exe" set "SDK=%ANDROID_SDK_ROOT%"
if "%SDK%"=="" if exist "%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" set "SDK=%LOCALAPPDATA%\Android\Sdk"
if "%SDK%"=="" if exist "C:\Android\Sdk\platform-tools\adb.exe" set "SDK=C:\Android\Sdk"
if "%SDK%"=="" if exist "C:\Program Files (x86)\Android\android-sdk\platform-tools\adb.exe" set "SDK=C:\Program Files (x86)\Android\android-sdk"
if "%SDK%"=="" if exist "C:\Program Files\Android\android-sdk\platform-tools\adb.exe" set "SDK=C:\Program Files\Android\android-sdk"

if "%SDK%"=="" (
    echo [ERROR] Could not find the Android SDK. Set ANDROID_HOME and try again.
    exit /b 1
)
set "ADB=%SDK%\platform-tools\adb.exe"

if not exist "%GRADLEW%" (
    echo [ERROR] gradlew.bat not found at "%GRADLEW%".
    echo         Run this script from the android-app project root.
    exit /b 1
)

echo ============================================================
echo  Step 1/5: Verify a device is connected
echo ============================================================
"%ADB%" start-server >nul 2>&1
set "DEVICE_FOUND=0"
for /f "skip=1 tokens=1,2" %%a in ('"%ADB%" devices') do (
    if "%%b"=="device" set "DEVICE_FOUND=1"
)
if "%DEVICE_FOUND%"=="0" (
    echo [ERROR] No booted emulator or connected device found.
    echo         Run start_emulator.bat ^(or start_all.bat^) first.
    exit /b 1
)
echo        Device connected - proceeding.

echo.
echo ============================================================
echo  Step 2/5: Clean the project
echo ============================================================
call "%GRADLEW%" clean
if errorlevel 1 (
    echo [FIX] Clean failed - stopping stale Gradle daemons and retrying...
    call "%GRADLEW%" --stop
    call "%GRADLEW%" clean
    if errorlevel 1 (
        echo [ERROR] Clean failed twice. Manual intervention required.
        exit /b 1
    )
)

echo.
echo ============================================================
echo  Step 3/5: Build the debug APK
echo ============================================================
call "%GRADLEW%" assembleDebug
if errorlevel 1 (
    echo [FIX] Build failed - stopping stale Gradle daemons and retrying once...
    call "%GRADLEW%" --stop
    call "%GRADLEW%" assembleDebug
    if errorlevel 1 (
        echo [ERROR] Build failed twice. Check the Gradle output above.
        exit /b 1
    )
)

if not exist "%APK_PATH%" (
    echo [ERROR] Build reported success but no APK found at "%APK_PATH%".
    exit /b 1
)

echo.
echo ============================================================
echo  Step 4/5: Install the app
echo ============================================================
set "INSTALL_TRIES=0"

:install_retry
set /a INSTALL_TRIES+=1

:: --- Fix "offline" adb state before installing ---
"%ADB%" devices | findstr /c:"offline" >nul
if %errorlevel%==0 (
    echo [FIX] Device is offline - restarting adb server...
    "%ADB%" kill-server
    timeout /t 2 /nobreak >nul
    "%ADB%" start-server
    "%ADB%" wait-for-device
)

"%ADB%" install -r "%APK_PATH%" > "%TEMP%\smartroad_install.log" 2>&1
type "%TEMP%\smartroad_install.log"
findstr /c:"Success" "%TEMP%\smartroad_install.log" >nul
if %errorlevel%==0 goto :install_done

findstr /c:"INSUFFICIENT_STORAGE" "%TEMP%\smartroad_install.log" >nul
if %errorlevel%==0 (
    echo [FIX] Insufficient storage - trimming caches and removing the previous build...
    "%ADB%" shell pm trim-caches 1024M >nul 2>&1
    "%ADB%" uninstall %PACKAGE% >nul 2>&1
)

findstr /c:"INSTALL_FAILED_UPDATE_INCOMPATIBLE" "%TEMP%\smartroad_install.log" >nul
if %errorlevel%==0 (
    echo [FIX] Existing install has a mismatched signature - removing it...
    "%ADB%" uninstall %PACKAGE% >nul 2>&1
)

findstr /c:"INSTALL_FAILED_ALREADY_EXISTS" "%TEMP%\smartroad_install.log" >nul
if %errorlevel%==0 (
    echo [FIX] Conflicting existing install - removing it...
    "%ADB%" uninstall %PACKAGE% >nul 2>&1
)

if %INSTALL_TRIES% LSS 3 (
    echo Retrying install... attempt %INSTALL_TRIES%
    goto :install_retry
)

echo [ERROR] Install failed after %INSTALL_TRIES% attempts. Manual intervention required.
exit /b 1

:install_done
echo        Install succeeded.

echo.
echo ============================================================
echo  Step 5/5: Launch the app and open Logcat
echo ============================================================
"%ADB%" shell am force-stop %PACKAGE% >nul 2>&1
"%ADB%" shell am start -n %LAUNCH_ACTIVITY%

:: Give the process a moment to spawn before grabbing its PID for Logcat.
timeout /t 2 /nobreak >nul
set "APP_PID="
for /f %%p in ('"%ADB%" shell pidof -s %PACKAGE% 2^>nul') do set "APP_PID=%%p"

if "%APP_PID%"=="" (
    echo [WARN] Could not resolve the app's PID - opening unfiltered Logcat instead.
    start "SmartRoad Logcat" cmd /k ""%ADB%" logcat"
) else (
    echo        Opening Logcat for PID %APP_PID%...
    start "SmartRoad Logcat" cmd /k ""%ADB%" logcat --pid=%APP_PID%"
)

echo.
echo SmartRoad is built, installed, and running. Logcat opened in a new window.
endlocal
