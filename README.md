# SmartRoad

**Crowdsourced Road Hazard Reporting and Monitoring System**

![Java](https://img.shields.io/badge/Java-17-007396?logo=openjdk&logoColor=white)
![Android](https://img.shields.io/badge/Android-minSdk%2026-3DDC84?logo=android&logoColor=white)
![PHP](https://img.shields.io/badge/PHP-8-777BB4?logo=php&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8-4479A1?logo=mysql&logoColor=white)
![Google Maps](https://img.shields.io/badge/Google%20Maps-SDK-4285F4?logo=googlemaps&logoColor=white)
![Retrofit](https://img.shields.io/badge/Retrofit-2.9-48B983)
![Material Design](https://img.shields.io/badge/Material%20Design-3-757575?logo=materialdesign&logoColor=white)

## Project Overview

SmartRoad is a mobile and web-based system that lets road users report hazards such as potholes, floods, and accidents directly from their phone, while administrators review, track, and resolve those reports through a web dashboard.

The project consists of three components:

- **Android mobile application** — used by the public to report and browse road hazards
- **PHP web administration system** — used by administrators to manage submitted reports
- **MySQL database** — stores users, hazard reports, and maintenance records

## System Architecture

```
Android Application
        ↓
  REST API (PHP)
        ↓
 MySQL Database
        ↓
 Admin Dashboard
```

The Android app communicates with the backend exclusively through a JSON REST API (`web-admin/api/`). The same MySQL database also backs the server-rendered admin dashboard, which reads and writes report data directly via PDO.

## Features

### Mobile

| Feature | Description |
|---|---|
| ✓ Login | Authenticates against the shared user database |
| ✓ Home Dashboard | Summary view of hazard activity |
| ✓ Hazard Map | Google Maps view of all reported hazards |
| ✓ Category & Status Filter | Filter hazards by type and resolution status |
| ✓ Report Hazard | Submit a new hazard report |
| ✓ GPS Capture | Attaches the reporter's current location automatically |
| ✓ Camera/Gallery Upload | Attach a photo from the camera or device gallery |
| ✓ Hazard Details | View full detail of a single hazard report |
| ✓ My Reports | List of hazards submitted by the current user |
| ✓ Profile Management | View and edit account details |
| ✓ Change Password | Update the account password |

### Admin

| Feature | Description |
|---|---|
| ✓ Dashboard | Overview statistics and recent report activity |
| ✓ Report Management | Central table of all submitted hazard reports |
| ✓ Search | Search reports by user or description |
| ✓ Filter | Filter the report list |
| ✓ Status Update | Update a report's investigation/resolution status |
| ✓ View Report | Inspect full report details, including photo and location |
| ✓ Delete Report | Remove a report and its associated photo |

## Technology Stack

**Android**
- Java
- Android Studio
- Google Maps SDK
- Retrofit
- OkHttp
- Material Design

**Backend**
- PHP
- MySQL
- REST API
- PDO
- Prepared Statements

**Database**
- MySQL

## Folder Structure

```
SmartRoad/
├── android-app/     # Android mobile application (Java, MVVM)
├── web-admin/        # PHP web administration system
├── database/         # MySQL schema and seed data
└── README.md
```

## Installation

1. Clone the project
2. Import `android-app/` into Android Studio
3. Place `web-admin/` into your Laragon (or XAMPP) `www` directory
4. Import the SQL files in `database/` into MySQL
5. Configure `android-app/local.properties` (Maps API key, backend IP)
6. Configure `web-admin/db.php` with your database credentials
7. Start Laragon and confirm the site loads at `localhost`
8. Launch the Android emulator and run the app

## Screenshots

### Mobile

(Home)

(Map)

(Report)

(Profile)

### Admin

(Dashboard)

(Manage Reports)
