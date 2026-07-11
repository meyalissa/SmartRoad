<?php
/**
 * SmartRoad — List every hazard report submitted by one mobile user.
 * GET ?user_id=<id>
 * Same JSON shape as get_hazards.php so the Android app can reuse its
 * existing Hazard model/adapters without any changes.
 */

header('Content-Type: application/json');
require_once __DIR__ . '/../db.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    http_response_code(405);
    echo json_encode(['error' => 'Method not allowed.']);
    exit;
}

$userId = isset($_GET['user_id']) ? trim($_GET['user_id']) : '';
if ($userId === '' || !ctype_digit($userId)) {
    http_response_code(400);
    echo json_encode(['error' => 'user_id is required.']);
    exit;
}

$scheme = (!empty($_SERVER['HTTPS']) && $_SERVER['HTTPS'] !== 'off') ? 'https' : 'http';
$host = $_SERVER['HTTP_HOST'] ?? 'localhost';
$uploadsBaseUrl = $scheme . '://' . $host . '/SmartRoad/web-admin/uploads/';

try {
    $stmt = $pdo->prepare("
        SELECT hr.id, hr.hazard_type, hr.description, hr.latitude, hr.longitude,
               hr.status, hr.photo, hr.reported_at, u.full_name
        FROM hazard_reports hr
        LEFT JOIN users u ON hr.user_id = u.id
        WHERE hr.user_id = ?
        ORDER BY hr.reported_at DESC
    ");
    $stmt->execute([$userId]);
    $rows = $stmt->fetchAll();

    $reports = [];
    foreach ($rows as $row) {
        $reports[] = [
            'id'          => (string) $row['id'],
            'type'        => $row['hazard_type'],
            'description' => $row['description'],
            'latitude'    => (string) $row['latitude'],
            'longitude'   => (string) $row['longitude'],
            'status'      => $row['status'],
            'datetime'    => $row['reported_at'] ? date('d/m/Y H:i', strtotime($row['reported_at'])) : '',
            'photo'       => $row['photo'] ? $uploadsBaseUrl . rawurlencode($row['photo']) : null,
            'reporter'    => $row['full_name'] ?? 'Anonymous',
        ];
    }

    http_response_code(200);
    echo json_encode($reports);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Server error. Please try again later.']);
}
