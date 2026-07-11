<?php
/**
 * SmartRoad — Mobile login API
 * POST username, password (form-urlencoded) -> JSON only, never HTML.
 * Authenticates against the `users` table (mobile app users), not admin_users.
 */

header('Content-Type: application/json');

require_once __DIR__ . '/../db.php';
require_once __DIR__ . '/_helpers.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    apiRespond(405, 'error', ['message' => 'Method not allowed.']);
}

$username = isset($_POST['username']) ? trim($_POST['username']) : '';
$password = isset($_POST['password']) ? (string) $_POST['password'] : '';

if ($username === '' || $password === '') {
    apiRespond(400, 'error', ['message' => 'Username and password are required.']);
}

try {
    $stmt = $pdo->prepare('SELECT id, username, password, full_name FROM users WHERE username = ?');
    $stmt->execute([$username]);
    $user = $stmt->fetch();

    // Same generic message for "no such user" and "wrong password" — never
    // reveal which one it was, so an attacker can't enumerate valid usernames.
    if (!$user || !password_verify($password, $user['password'])) {
        apiRespond(401, 'error', ['message' => 'Invalid username or password.']);
    }

    apiRespond(200, 'success', [
        'id'       => (string) $user['id'],
        'fullname' => $user['full_name'],
        'username' => $user['username'],
        'message'  => 'Login successful.',
    ]);
} catch (PDOException $e) {
    apiRespond(500, 'error', ['message' => 'Server error. Please try again later.']);
}
