# SmartRoad

Crowdsourced road hazard reporting and monitoring system (Android, Java, MVVM).

Course: ICT602 Mobile Technology — Group Project.

## Developers
- Melissa Sofia Binti Shahran — 2025397307
- Khaulah Kareema Binti Sofian — 2025101461
- Marsya Qistina Binti Meor Rusydi — 2025106901
- Nur Farah Aisyah Binti Sharudin — 2025178939

Project / website: https://github.com/meyalissa/SmartRoad

## Tech stack
- Java, MVVM + Repository pattern, View Binding
- Retrofit (network), Glide (images)
- Google Maps SDK + Fused Location Provider
- Material Design 3 (centralized theming, light + dark)
- minSdk 26, compileSdk/targetSdk 34

## Setup (3 steps)
1. **Open in Android Studio** (Giraffe / Hedgehog or newer). Let it sync — it will
   download the Gradle wrapper and dependencies automatically.

2. **Add your Google Maps API key.** Open `local.properties` and set:
   ```
   MAPS_API_KEY=YOUR_KEY_HERE
   ```
   Get a key at Google Cloud Console → enable **Maps SDK for Android**.
   (The key is read from local.properties and injected into the manifest — it is
   never committed to source.)

3. **Point at your backend (optional).** Open
   `app/src/main/java/com/smartroad/network/ApiClient.java`:
   - Set `BASE_URL` to your server (must end with `/`).
   - Set `DEMO_MODE = false` once your server is live.

### Demo mode
`DEMO_MODE = true` (default) makes the whole app work **without a backend** using
built-in sample data — ideal for recording the demo video. Login accepts any
non-empty username/password. Switch it off to use your real PHP/MySQL server.

## API contract expected by the app
- `POST login`  (form fields: username, password) → `{"status":"success","id":"1","fullname":"...","username":"..."}`
- `GET  hazards` → `[{"id","type","latitude","longitude","status","description","datetime","photo","reporter"}, ...]`
- `POST report` (multipart: hazard_type, description, latitude, longitude, datetime, photo) → `{"status":"success"}`
- `GET  profile?id=` → `{"fullname","username","photo","total_reports","resolved_reports","pending_reports"}`

## Marker colors
Pothole = Red, Flood = Blue, Accident = Orange, Fallen Tree = Green,
Damaged Road Sign = Purple, Broken Traffic Light = Yellow.

## Rebranding
Edit **one file** to change brand colors app-wide:
`app/src/main/java/com/smartroad/config/BrandColors.java`
(and mirror the few brand hex values in `res/values/colors.xml` for XML previews).
