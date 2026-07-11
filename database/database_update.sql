-- ============================================================
-- SmartRoad Database Update Script
-- Run this AFTER database.sql on an existing installation to add
-- road-maintenance tracking and login rate-limiting.
-- Safe to re-run (uses IF NOT EXISTS).
-- Does NOT modify any existing table structure or data.
-- ============================================================

USE smartroad;

-- ------------------------------------------------------------
-- One hazard report may have zero or one maintenance record.
-- The UNIQUE constraint on hazard_report_id enforces that.
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS maintenance_records (
  id                 INT AUTO_INCREMENT PRIMARY KEY,
  hazard_report_id   INT NOT NULL UNIQUE,
  maintenance_team   VARCHAR(150) DEFAULT NULL,
  maintenance_notes  TEXT,
  repair_date        DATE DEFAULT NULL,
  completed_date     DATE DEFAULT NULL,
  created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (hazard_report_id) REFERENCES hazard_reports(id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- Login rate limiting for the mobile login API (login.php).
-- A database table is used instead of PHP sessions because the mobile
-- app's HTTP client does not persist a session cookie between requests,
-- so session-based counters would never accumulate across attempts.
-- Only failed attempts are recorded; a successful login clears them.
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS login_attempts (
  id            INT AUTO_INCREMENT PRIMARY KEY,
  username      VARCHAR(50) NOT NULL,
  attempted_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_login_attempts_username (username, attempted_at)
);
