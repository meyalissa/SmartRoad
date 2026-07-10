<?php
/* ============================================================
   GET /get_report.php
   Returns every hazard report, joined with the reporting user's
   name, shaped exactly like the mock objects the front-end
   used to use — so manage-report.php's JS didn't need to change
   its filtering/sorting/pagination logic at all.
   ============================================================ */

require_once __DIR__ . '/db.php';
require_once 'auth.php';

header('Content-Type: application/json');

try {
    $stmt = $pdo->query("
        SELECT
            hr.id,
            u.full_name    AS user,
            hr.reported_at AS date,
            hr.hazard_type AS hazard,
            hr.latitude    AS lat,
            hr.longitude   AS lng,
            hr.status      AS status,
            hr.photo       AS photo,
            hr.description AS description,
            hr.user_agent  AS user_agent
        FROM hazard_reports hr
        JOIN users u ON hr.user_id = u.id
        ORDER BY hr.reported_at DESC
    ");

    $reports = $stmt->fetchAll();

    foreach ($reports as &$r) {
        // DECIMAL columns come back from PDO as strings — cast to numbers
        $r['lat'] = (float) $r['lat'];
        $r['lng'] = (float) $r['lng'];
        // MySQL TIMESTAMP is "YYYY-MM-DD HH:MM:SS" — convert to ISO 8601
        // so JavaScript's `new Date(r.date)` parses it reliably in every browser
        $r['date'] = str_replace(' ', 'T', $r['date']);
    }
    unset($r);

    echo json_encode($reports);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Could not load reports: ' . $e->getMessage()]);
}