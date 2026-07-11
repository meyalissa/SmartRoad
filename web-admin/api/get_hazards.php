<?php
/**
 * SmartRoad — Public hazard retrieval API
 * GET only, no auth. Returns every hazard report, newest first, joined
 * with `users` for the reporter's name. JSON only, never HTML.
 * Response shape matches the Android Hazard model exactly:
 * id, type, description, latitude, longitude, status, datetime, photo, reporter.
 */

header('Content-Type: application/json');

require_once __DIR__ . '/../db.php';
require_once __DIR__ . '/_helpers.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    apiRespond(405, 'error', ['message' => 'Method not allowed.']);
}

$uploadsBaseUrl = apiOrigin() . 'uploads/';

try {
    $stmt = $pdo->query(
        "SELECT hr.id, hr.hazard_type, hr.description, hr.latitude, hr.longitude,
                hr.status, hr.photo, hr.reported_at, u.full_name
         FROM hazard_reports hr
         LEFT JOIN users u ON hr.user_id = u.id
         ORDER BY hr.reported_at DESC"
    );
    $rows = $stmt->fetchAll();

    $hazards = [];
    foreach ($rows as $row) {
        $hazards[] = [
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

    // Always a bare JSON array on success — Retrofit/Gson parses this
    // response directly as List<Hazard>, so it must never be wrapped in an
    // envelope object the way error responses are.
    http_response_code(200);
    echo json_encode($hazards);
} catch (PDOException $e) {
    apiRespond(500, 'error', ['message' => 'Server error. Please try again later.']);
}
