<?php
/**
 * SmartRoad — Mobile login API
 * POST username, password (form-urlencoded) -> JSON only, never HTML.
 * Authenticates against the `users` table (mobile app users), not admin_users.
 *
 * Rate limiting: the mobile HTTP client doesn't keep a session cookie
 * between requests, so failed attempts are counted in the login_attempts
 * table (see database_update.sql) instead of $_SESSION.
 */

header('Content-Type: application/json');

require_once __DIR__ . '/../db.php';
require_once __DIR__ . '/_helpers.php';

const MAX_FAILED_ATTEMPTS = 5;
const LOCKOUT_MINUTES = 5;

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    apiRespond(405, 'error', ['message' => 'Method not allowed.']);
}

$username = isset($_POST['username']) ? trim($_POST['username']) : '';
$password = isset($_POST['password']) ? (string) $_POST['password'] : '';

if ($username === '' || $password === '') {
    apiRespond(400, 'error', ['message' => 'Username and password are required.']);
}

try {
    $stmt = $pdo->prepare('
        SELECT COUNT(*) FROM login_attempts
        WHERE username = ? AND attempted_at > (NOW() - INTERVAL ? MINUTE)
    ');
    $stmt->execute([$username, LOCKOUT_MINUTES]);
    if ((int) $stmt->fetchColumn() >= MAX_FAILED_ATTEMPTS) {
        apiRespond(429, 'error', [
            'message' => 'Too many failed login attempts. Please try again in ' . LOCKOUT_MINUTES . ' minutes.',
        ]);
    }

    $stmt = $pdo->prepare('SELECT id, username, password, full_name FROM users WHERE username = ?');
    $stmt->execute([$username]);
    $user = $stmt->fetch();

    // Same generic message for "no such user" and "wrong password" — never
    // reveal which one it was, so an attacker can't enumerate valid usernames.
    if (!$user || !password_verify($password, $user['password'])) {
        $stmt = $pdo->prepare('INSERT INTO login_attempts (username) VALUES (?)');
        $stmt->execute([$username]);
        apiRespond(401, 'error', ['message' => 'Invalid username or password.']);
    }

    // Successful login — clear this username's slate so a legitimate user
    // who mistyped their password a few times isn't left half-locked-out.
    $stmt = $pdo->prepare('DELETE FROM login_attempts WHERE username = ?');
    $stmt->execute([$username]);

    apiRespond(200, 'success', [
        'id'       => (string) $user['id'],
        'fullname' => $user['full_name'],
        'username' => $user['username'],
        'message'  => 'Login successful.',
    ]);
} catch (PDOException $e) {
    apiRespond(500, 'error', ['message' => 'Server error. Please try again later.']);
}
