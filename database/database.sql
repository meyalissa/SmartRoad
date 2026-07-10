-- ============================================================
-- SmartRoad Database Schema
-- Import this file in phpMyAdmin (or run via mysql CLI) BEFORE
-- opening login.php for the first time.
-- ============================================================

CREATE DATABASE IF NOT EXISTS smartroad
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE smartroad;

-- ------------------------------------------------------------
-- Admin accounts (web dashboard login)
-- ------------------------------------------------------------
CREATE TABLE admin_users (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  username    VARCHAR(50) NOT NULL UNIQUE,
  password    VARCHAR(255) NOT NULL,   -- bcrypt hash, never store plain text
  full_name   VARCHAR(100) NOT NULL,
  profile_picture VARCHAR(255) DEFAULT NULL,  -- filename stored in /uploads/admin
  created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ------------------------------------------------------------
-- Mobile app users (people who submit hazard reports)
-- ------------------------------------------------------------
CREATE TABLE users (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  username    VARCHAR(50) NOT NULL UNIQUE,
  password    VARCHAR(255) NOT NULL,
  full_name   VARCHAR(100) NOT NULL,
  email       VARCHAR(100),
  profile_picture VARCHAR(255) DEFAULT NULL,  -- filename stored in /uploads/users
  created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- ------------------------------------------------------------
-- Hazard reports submitted from the mobile app
-- ------------------------------------------------------------
CREATE TABLE hazard_reports (
  id           INT AUTO_INCREMENT PRIMARY KEY,
  user_id      INT NOT NULL,
  hazard_type  ENUM('Pothole','Flood','Accident','Fallen Tree','Damaged Road Sign','Broken Traffic Light') NOT NULL,
  description  TEXT,
  photo        VARCHAR(255),          -- filename stored in /uploads
  latitude     DECIMAL(10,7) NOT NULL,
  longitude    DECIMAL(10,7) NOT NULL,
  status       ENUM('New','Under Investigation','Resolved') NOT NULL DEFAULT 'New',
  user_agent   VARCHAR(255),          -- e.g. "Android 15" — captured by mobile app on submit
  reported_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Index to make dashboard/manage-reports filtering fast
CREATE INDEX idx_hazard_status ON hazard_reports(status);
CREATE INDEX idx_hazard_type   ON hazard_reports(hazard_type);
CREATE INDEX idx_hazard_date   ON hazard_reports(reported_at);

-- ------------------------------------------------------------
-- Seed data
-- ------------------------------------------------------------

-- Admin login -> username: admin | password: admin123
INSERT INTO admin_users (username, password, full_name) VALUES
('admin', '$2b$12$qNhKBg9FTtUZzDHKHg4p5eZPuVl9B5.gCy1TEmdZCx1gvVFGADk4.', 'System Administrator');

-- Sample mobile app users -> password for both: password123
INSERT INTO users (username, password, full_name, email) VALUES
('idayati', '$2b$12$tGrGXQInNnMeIMwr4J2apOyEXA6JqNbjmPHIxz6vTlwEfCCvIfajW', 'Idayati Mazlan', 'idayati@example.com'),
('ali',     '$2b$12$tGrGXQInNnMeIMwr4J2apOyEXA6JqNbjmPHIxz6vTlwEfCCvIfajW', 'Ali Ahmad',      'ali@example.com'),
('abu',     '$2b$12$tGrGXQInNnMeIMwr4J2apOyEXA6JqNbjmPHIxz6vTlwEfCCvIfajW', 'Abu Bakar',      'abu@example.com');

-- Sample hazard reports so the dashboard isn't empty on first run
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent) VALUES
(2, 'Flood',   'Road flooded after heavy rain, water level knee-high near the junction.', NULL, 2.3115000, 102.3218000, 'Under Investigation', 'Android 15'),
(3, 'Pothole', 'Large pothole causing traffic congestion during peak hours.',             NULL, 2.3120000, 102.3225000, 'New',                 'Android 14'),
(1, 'Accident','Minor collision blocking the left lane.',                                 NULL, 2.3098000, 102.3190000, 'Resolved',            'Android 15'),
(2, 'Fallen Tree', 'Tree fell across the road after last night\'s storm.',                NULL, 2.3140000, 102.3260000, 'New',                 'Android 13') ;
