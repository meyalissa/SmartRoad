<?php
/**
 * SmartRoad Admin — Create or update the maintenance record for one hazard
 * report. A report has zero or one maintenance record (hazard_report_id is
 * UNIQUE), so this is a single upsert: INSERT ... ON DUPLICATE KEY UPDATE.
 * POST: hazard_report_id, maintenance_team, repair_date, completed_date, maintenance_notes
 */

require_once __DIR__ . '/db.php';
require_once 'auth.php';

header('Content-Type: application/json');

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(['success' => false, 'message' => 'Method not allowed.']);
    exit;
}

$hazardReportId = isset($_POST['hazard_report_id']) ? trim($_POST['hazard_report_id']) : '';
$team           = isset($_POST['maintenance_team']) ? trim($_POST['maintenance_team']) : '';
$notes          = isset($_POST['maintenance_notes']) ? trim($_POST['maintenance_notes']) : '';
$repairDate     = isset($_POST['repair_date']) ? trim($_POST['repair_date']) : '';
$completedDate  = isset($_POST['completed_date']) ? trim($_POST['completed_date']) : '';

if ($hazardReportId === '' || !ctype_digit($hazardReportId)) {
    http_response_code(400);
    echo json_encode(['success' => false, 'message' => 'Invalid report id.']);
    exit;
}

// Team/notes/dates are all optional (a record can be created before any of
// this is known yet), but any date that IS supplied must be a real date.
foreach (['repair_date' => $repairDate, 'completed_date' => $completedDate] as $label => $value) {
    if ($value !== '' && !DateTime::createFromFormat('Y-m-d', $value)) {
        http_response_code(400);
        echo json_encode(['success' => false, 'message' => "Invalid $label."]);
        exit;
    }
}

try {
    $stmt = $pdo->prepare('SELECT id FROM hazard_reports WHERE id = ?');
    $stmt->execute([$hazardReportId]);
    if (!$stmt->fetch()) {
        http_response_code(404);
        echo json_encode(['success' => false, 'message' => 'Report not found.']);
        exit;
    }

    $stmt = $pdo->prepare('
        INSERT INTO maintenance_records
            (hazard_report_id, maintenance_team, maintenance_notes, repair_date, completed_date)
        VALUES
            (:hazard_report_id, :maintenance_team, :maintenance_notes, :repair_date, :completed_date)
        ON DUPLICATE KEY UPDATE
            maintenance_team = VALUES(maintenance_team),
            maintenance_notes = VALUES(maintenance_notes),
            repair_date = VALUES(repair_date),
            completed_date = VALUES(completed_date)
    ');
    $stmt->execute([
        ':hazard_report_id'  => $hazardReportId,
        ':maintenance_team'  => $team !== '' ? $team : null,
        ':maintenance_notes' => $notes !== '' ? $notes : null,
        ':repair_date'       => $repairDate !== '' ? $repairDate : null,
        ':completed_date'    => $completedDate !== '' ? $completedDate : null,
    ]);

    echo json_encode(['success' => true, 'message' => 'Maintenance information saved.']);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['success' => false, 'message' => 'Server error. Please try again later.']);
}
