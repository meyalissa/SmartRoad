<?php
/**
 * SmartRoad — Mobile login API
 * POST username, password (form-urlencoded) -> JSON only, never HTML.
 * Authenticates against the `users` table (mobile app users), not admin_users.
 */

header('Content-Type: application/json');

require_once __DIR__ . '/../db.php';

function respond(int $httpCode, string $status, ?string $id, ?string $fullname, ?string $username, string $message): void {
    http_response_code($httpCode);
    echo json_encode([
        'status'   => $status,
        'id'       => $id,
        'fullname' => $fullname,
        'username' => $username,
        'message'  => $message,
    ]);
    exit;
}

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    respond(405, 'error', null, null, null, 'Method not allowed.');
}

$username = isset($_POST['username']) ? trim($_POST['username']) : '';
$password = isset($_POST['password']) ? (string) $_POST['password'] : '';

if ($username === '' || $password === '') {
    respond(400, 'error', null, null, null, 'Username and password are required.');
}

try {
    $stmt = $pdo->prepare('SELECT id, username, password, full_name FROM users WHERE username = ?');
    $stmt->execute([$username]);
    $user = $stmt->fetch();

    if (!$user || !password_verify($password, $user['password'])) {
        respond(401, 'error', null, null, null, 'Invalid username or password.');
    }

    respond(200, 'success', (string) $user['id'], $user['full_name'], $user['username'], 'Login successful.');
} catch (PDOException $e) {
    respond(500, 'error', null, null, null, 'Server error. Please try again later.');
}
