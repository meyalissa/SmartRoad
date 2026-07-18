<?php
/**
 * GET /get_report.php
 * Returns all hazard reports as JSON, joined with the reporting user and
 * any associated maintenance record.
 */

require_once __DIR__ . '/db.php';
require_once 'auth.php';

header('Content-Type: application/json');

try {
    // LEFT JOINed since a report has zero or one maintenance_records row —
    // the m.* columns come back NULL when none exists yet.
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
            hr.user_agent  AS user_agent,
            m.maintenance_team  AS maintenance_team,
            m.maintenance_notes AS maintenance_notes,
            m.repair_date       AS repair_date,
            m.completed_date    AS completed_date
        FROM hazard_reports hr
        JOIN users u ON hr.user_id = u.id
        LEFT JOIN maintenance_records m ON m.hazard_report_id = hr.id
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