<?php
/**
 * SmartRoad — Fetch the logged-in mobile user's profile + report statistics.
 * GET ?user_id=<id>
 */

header('Content-Type: application/json');
require_once __DIR__ . '/../db.php';
require_once __DIR__ . '/_helpers.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    apiRespond(405, 'error', ['message' => 'Method not allowed.']);
}

$userId = isset($_GET['user_id']) ? trim($_GET['user_id']) : '';
if ($userId === '' || !ctype_digit($userId)) {
    apiRespond(400, 'error', ['message' => 'user_id is required.']);
}

try {
    $stmt = $pdo->prepare('SELECT id, username, full_name, email, profile_picture, created_at FROM users WHERE id = ?');
    $stmt->execute([$userId]);
    $user = $stmt->fetch();

    if (!$user) {
        apiRespond(404, 'error', ['message' => 'User not found.']);
    }

    // MySQL evaluates each `status = '...'` comparison to 0 or 1, so SUM()
    // over it counts how many rows matched — one query, four counts, always
    // in sync with each other (no risk of running four separate queries at
    // slightly different times and getting a total that doesn't add up).
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

    $photoUrl = $user['profile_picture']
        ? apiOrigin() . 'uploads/users/' . rawurlencode($user['profile_picture'])
        : null;

    apiRespond(200, 'success', [
        'id'                    => (string) $user['id'],
        'fullname'              => $user['full_name'],
        'username'              => $user['username'],
        'email'                 => $user['email'],
        'photo'                 => $photoUrl,
        'created_at'            => $user['created_at'] ? date('d M Y', strtotime($user['created_at'])) : null,
        'total_reports'         => (int) $stats['total'],
        'pending_reports'       => (int) $stats['pending'],
        'investigating_reports' => (int) $stats['investigating'],
        'resolved_reports'      => (int) $stats['resolved'],
    ]);
} catch (PDOException $e) {
    apiRespond(500, 'error', ['message' => 'Server error. Please try again later.']);
}
