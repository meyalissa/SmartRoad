# SmartRoad Web Administration

![PHP](https://img.shields.io/badge/PHP-8-777BB4?logo=php&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8-4479A1?logo=mysql&logoColor=white)
![Laragon](https://img.shields.io/badge/Laragon%2FXAMPP-compatible-2E8B57)

## Overview

The SmartRoad web administration system is a PHP + MySQL dashboard used by administrators to review and manage hazard reports submitted through the Android application. It also exposes the JSON REST API consumed by the mobile app.

## Features

- **Dashboard** — overview statistics (total users, total reports, open/resolved counts) and a list of recently submitted reports
- **Manage Reports** — searchable, filterable table of all hazard reports
- **Status Updates** — update a report's investigation/resolution status and attach maintenance details (team, repair date, completion date, notes)
- **Search** — search reports by user or description
- **Filtering** — filter the report list
- **Delete Report** — remove a report and its associated photo from disk

## Project Structure

```
web-admin/
├── api/          # JSON REST endpoints consumed by the Android app
├── css/          # Stylesheets
├── js/           # Client-side scripts
├── uploads/      # Uploaded hazard and profile photos
├── db.php        # Shared PDO database connection
├── auth.php      # Admin session guard
├── sidebar.php   # Shared admin navigation include
└── *.php         # Admin dashboard pages
```

## Database

- **Engine:** MySQL
- **Access layer:** PDO with prepared statements
- **Schema:** `database/database.sql` (core schema), `database/database_update.sql` (maintenance tracking and login rate-limiting), `database/dummy_data.sql` (sample data)
- **Core tables:** `admin_users`, `users`, `hazard_reports`, `maintenance_records`, `login_attempts`

Compatible with both Laragon and XAMPP local development environments.

## Configuration

1. Edit `db.php` with your database host, name, username, and password.
2. Import the SQL files in `database/` into your MySQL server, in order: `database.sql`, then `database_update.sql`, then optionally `dummy_data.sql`.
3. Place the `web-admin/` folder inside your local server's web root (e.g. Laragon's `www/SmartRoad/web-admin`).
4. Start your local server and open the dashboard at `http://localhost/SmartRoad/web-admin/login.php`.

## Security

- **Prepared Statements** — all database queries use PDO prepared statements to prevent SQL injection
- **Input Validation** — request parameters are validated and sanitized before use
- **Image Validation** — uploaded photos are content-sniffed by MIME type (not filename/extension) and size-limited before being stored, preventing disguised file uploads
- **Password Hashing** — passwords are hashed with `password_hash()` and verified with `password_verify()`, both for admin logins and mobile app user accounts

## REST APIs

All endpoints live under `web-admin/api/` and return JSON.

| Method | Endpoint | Description |
|---|---|---|
| POST | `login.php` | Authenticates a mobile app user |
| GET | `get_hazards.php` | Retrieves all hazard reports |
| GET | `my_reports.php` | Retrieves hazard reports submitted by a specific user |
| GET | `get_report_details.php` | Retrieves full details for a single hazard report |
| POST | `report_hazard.php` | Submits a new hazard report, including photo upload |
| GET | `profile.php` | Retrieves a user's profile information |
| POST | `update_profile.php` | Updates a user's profile details and photo |
| POST | `change_password.php` | Changes a user's account password |
