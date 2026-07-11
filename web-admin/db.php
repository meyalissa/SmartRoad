<?php
/**
 * SmartRoad — Database connection
 * Place this file directly in web-admin/ (same folder as login.php,
 * dashboard.php, manage-report.php, hazard-form.php).
 * Every page that needs the DB does: require_once 'db.php';
 */

define('DB_HOST', 'localhost');
define('DB_NAME', 'smartroad');
define('DB_USER', 'root');
define('DB_PASS', '');   // default XAMPP root password is empty

try {
    $pdo = new PDO(
        "mysql:host=" . DB_HOST . ";dbname=" . DB_NAME . ";charset=utf8mb4",
        DB_USER,
        DB_PASS,
        [
            PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
            PDO::ATTR_EMULATE_PREPARES   => false,
        ]
    );
} catch (PDOException $e) {
    error_log('SmartRoad DB connection failed: ' . $e->getMessage());
    http_response_code(500);
    die('Database connection failed. Please try again later.');
}