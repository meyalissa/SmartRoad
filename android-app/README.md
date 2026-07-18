# SmartRoad Android Application

![Java](https://img.shields.io/badge/Java-17-007396?logo=openjdk&logoColor=white)
![Android](https://img.shields.io/badge/Android-minSdk%2026-3DDC84?logo=android&logoColor=white)
![Google Maps](https://img.shields.io/badge/Google%20Maps-SDK-4285F4?logo=googlemaps&logoColor=white)
![Retrofit](https://img.shields.io/badge/Retrofit-2.9-48B983)
![Material Design](https://img.shields.io/badge/Material%20Design-3-757575?logo=materialdesign&logoColor=white)

## Overview

The SmartRoad Android application allows road users to report hazards such as potholes, floods, and accidents, browse hazards reported by others on a live map, and track the status of their own submissions. It communicates with the SmartRoad PHP backend over a JSON REST API.

## Features

- Login and session management
- Home dashboard
- Interactive hazard map (Google Maps)
- Category and status filtering
- Hazard reporting with GPS location capture
- Photo attachment via camera or gallery
- Hazard detail view
- My Reports (user's own submission history)
- Profile management and photo upload
- Change password

## Architecture

The app follows an **MVVM (Model-View-ViewModel) + Repository** architecture with View Binding:

```
UI (Activity/Fragment)
      ↓
  ViewModel
      ↓
 Repository
      ↓
Retrofit / ApiService
      ↓
  PHP REST API
```

## Packages

| Package | Responsibility |
|---|---|
| `ui/` | Activities and Fragments for each screen |
| `repository/` | Mediates between ViewModels and the network layer |
| `network/` | Retrofit client and API service definitions |
| `model/` | Data models mapped from API responses |
| `util/` | Shared helpers (session, image handling, location, markers) |
| `viewmodel/` | Screen state and business logic, exposed via LiveData |
| `notifications/` | Firebase Cloud Messaging service |

## Dependencies

| Library | Purpose |
|---|---|
| Retrofit + OkHttp | REST API communication |
| Glide | Image loading and caching |
| Google Maps SDK + Fused Location Provider | Hazard map and GPS capture |
| Material Components | UI theming and widgets |
| Firebase (Messaging, Crashlytics, Analytics) | Push notification infrastructure, crash reporting, analytics |
| AndroidX Lifecycle / Navigation | ViewModel, LiveData, fragment navigation |

## SDK Versions

| | Version |
|---|---|
| Minimum SDK | 26 |
| Target SDK | 34 |
| Compile SDK | 34 |

## Build Instructions

1. **Open the project in Android Studio** and let it sync — the Gradle wrapper and dependencies are downloaded automatically.

2. **Add a Google Maps API key.** Open `local.properties` in the project root and set:
   ```
   MAPS_API_KEY=YOUR_KEY_HERE
   ```
   Create a key in Google Cloud Console with **Maps SDK for Android** enabled. The key is read from `local.properties` at build time and injected into the manifest — it is never hard-coded in source.

3. **Connect to the backend.** Debug builds resolve the API base URL automatically:
   - On an emulator, requests are routed to the host machine's Laragon/XAMPP server.
   - On a physical device, set `DEVICE_LAN_IP` in `local.properties` to your development machine's LAN IP so the device can reach the backend over Wi-Fi.

   Release builds use `BASE_URL` in `app/build.gradle`, which should point to the deployed production API host.

## Permissions Used

| Permission | Purpose |
|---|---|
| `INTERNET` / `ACCESS_NETWORK_STATE` | REST API communication |
| `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION` | GPS capture for hazard reports and map centering |
| `CAMERA` | Capturing a hazard photo directly from the app |
| `READ_MEDIA_IMAGES` / `READ_EXTERNAL_STORAGE` | Selecting a photo from the device gallery |

## Known Notes

- The app uses REST APIs exclusively; it has no local database or offline mode.
- A running backend server (see `web-admin/`) is required for the app to function.
- The hazard map requires the Google Maps SDK and a valid Maps API key.

## Local Development Scripts (Windows)

Batch scripts are provided to automate the emulator + build + install + launch cycle.

| Script | What it does |
|---|---|
| `start_emulator.bat` | Auto-detects the SDK and installed AVDs, boots one, and waits for a healthy boot. |
| `start_project.bat` | Builds the debug APK, installs it on the connected device, and launches the app with a filtered Logcat window. |
| `start_all.bat` | Runs `start_emulator.bat` followed by `start_project.bat`. |
| `stop_all.bat` | Stops the app, closes Logcat, and shuts down the emulator and adb server. |

```
start_all.bat
stop_all.bat
```

To target a specific AVD non-interactively:
```
set AVD_NAME=Pixel_8_API_35
start_all.bat
```

### Manual commands

```
:: List available emulators
%ANDROID_HOME%\emulator\emulator.exe -list-avds

:: Install the debug APK
adb install -r app\build\outputs\apk\debug\app-debug.apk

:: Launch the app
adb shell am start -n com.smartroad/.ui.splash.SplashActivity

:: View Logcat for the app only
adb logcat --pid=$(adb shell pidof -s com.smartroad)

:: Restart adb (fixes most "offline"/"unauthorized" states)
adb kill-server
adb start-server
```
