<?php
/**
 * SmartRoad — Fetch the logged-in mobile user's profile + report statistics.
 * GET ?user_id=<id>
 */

header('Content-Type: application/json');
require_once __DIR__ . '/../db.php';

function respond(int $httpCode, string $status, array $extra = []): void {
    http_response_code($httpCode);
    echo json_encode(array_merge(['status' => $status], $extra));
    exit;
}

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    respond(405, 'error', ['message' => 'Method not allowed.']);
}

$userId = isset($_GET['user_id']) ? trim($_GET['user_id']) : '';
if ($userId === '' || !ctype_digit($userId)) {
    respond(400, 'error', ['message' => 'user_id is required.']);
}

try {
    $stmt = $pdo->prepare('SELECT id, username, full_name, email, profile_picture, created_at FROM users WHERE id = ?');
    $stmt->execute([$userId]);
    $user = $stmt->fetch();

    if (!$user) {
        respond(404, 'error', ['message' => 'User not found.']);
    }

    $statsStmt = $pdo->prepare("
        SELECT
            COUNT(*) AS total,
            SUM(status = 'New') AS pending,
            SUM(status = 'Under Investigation') AS investigating,
            SUM(status = 'Resolved') AS resolved
        FROM hazard_reports
        WHERE user_id = ?
    ");
    $statsStmt->execute([$userId]);
    $stats = $statsStmt->fetch();

    $scheme = (!empty($_SERVER['HTTPS']) && $_SERVER['HTTPS'] !== 'off') ? 'https' : 'http';
    $host = $_SERVER['HTTP_HOST'] ?? 'localhost';
    $photoUrl = $user['profile_picture']
        ? $scheme . '://' . $host . '/SmartRoad/web-admin/uploads/users/' . rawurlencode($user['profile_picture'])
        : null;

    respond(200, 'success', [
        'id'                   => (string) $user['id'],
        'fullname'             => $user['full_name'],
        'username'             => $user['username'],
        'email'                => $user['email'],
        'photo'                => $photoUrl,
        'created_at'           => $user['created_at'] ? date('d M Y', strtotime($user['created_at'])) : null,
        'total_reports'        => (int) $stats['total'],
        'pending_reports'      => (int) $stats['pending'],
        'investigating_reports'=> (int) $stats['investigating'],
        'resolved_reports'     => (int) $stats['resolved'],
    ]);
} catch (PDOException $e) {
    respond(500, 'error', ['message' => 'Server error. Please try again later.']);
}
