<?php
/**
 * SmartRoad — Fetch one hazard report's live status plus its maintenance
 * record (if any). Used by the mobile app's Hazard Detail screen so status
 * and maintenance info are always current, not just whatever was cached
 * from the last list refresh. Same field names as get_hazards.php, with an
 * added "maintenance" object (null when no record exists yet).
 * GET ?id=<report id>
 */

header('Content-Type: application/json');
require_once __DIR__ . '/../db.php';
require_once __DIR__ . '/_helpers.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    apiRespond(405, 'error', ['message' => 'Method not allowed.']);
}

$id = isset($_GET['id']) ? trim($_GET['id']) : '';
if ($id === '' || !ctype_digit($id)) {
    apiRespond(400, 'error', ['message' => 'id is required.']);
}

$uploadsBaseUrl = apiOrigin() . 'uploads/';

try {
    $stmt = $pdo->prepare("
        SELECT hr.id, hr.hazard_type, hr.description, hr.latitude, hr.longitude,
               hr.status, hr.photo, hr.reported_at, u.full_name,
               m.maintenance_team, m.maintenance_notes, m.repair_date, m.completed_date
        FROM hazard_reports hr
        LEFT JOIN users u ON hr.user_id = u.id
        LEFT JOIN maintenance_records m ON m.hazard_report_id = hr.id
        WHERE hr.id = ?
    ");
    $stmt->execute([$id]);
    $row = $stmt->fetch();

    if (!$row) {
        apiRespond(404, 'error', ['message' => 'Report not found.']);
    }

    $hasMaintenance = $row['maintenance_team'] !== null || $row['maintenance_notes'] !== null
        || $row['repair_date'] !== null || $row['completed_date'] !== null;

    http_response_code(200);
    echo json_encode([
        'id'          => (string) $row['id'],
        'type'        => $row['hazard_type'],
        'description' => $row['description'],
        'latitude'    => (string) $row['latitude'],
        'longitude'   => (string) $row['longitude'],
        'status'      => $row['status'],
        'datetime'    => $row['reported_at'] ? date('d/m/Y H:i', strtotime($row['reported_at'])) : '',
        'photo'       => $row['photo'] ? $uploadsBaseUrl . rawurlencode($row['photo']) : null,
        'reporter'    => $row['full_name'] ?? 'Anonymous',
        'maintenance' => $hasMaintenance ? [
            'team'          => $row['maintenance_team'],
            'notes'         => $row['maintenance_notes'],
            'repair_date'   => $row['repair_date'] ? date('d/m/Y', strtotime($row['repair_date'])) : null,
            'completed_date'=> $row['completed_date'] ? date('d/m/Y', strtotime($row['completed_date'])) : null,
        ] : null,
    ]);
} catch (PDOException $e) {
    apiRespond(500, 'error', ['message' => 'Server error. Please try again later.']);
}
