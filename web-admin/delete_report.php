<?php
/* ============================================================
   DELETE /delete_report.php?id=123
   Deletes the report row, and its photo file from /uploads if
   one exists.
   ============================================================ */

require_once __DIR__ . '/db.php';
require_once 'auth.php';


header('Content-Type: application/json');

$id = isset($_GET['id']) ? (int) $_GET['id'] : 0;

if ($id <= 0) {
    http_response_code(400);
    echo json_encode(['error' => 'Invalid report id.']);
    exit;
}

try {
    // Look up the photo filename first so we can remove the file too
    $stmt = $pdo->prepare("SELECT photo FROM hazard_reports WHERE id = ?");
    $stmt->execute([$id]);
    $report = $stmt->fetch();

    $stmt = $pdo->prepare("DELETE FROM hazard_reports WHERE id = ?");
    $stmt->execute([$id]);

    if ($stmt->rowCount() === 0) {
        http_response_code(404);
        echo json_encode(['error' => 'Report not found.']);
        exit;
    }

    // NOTE: adjust this path if your uploads folder lives somewhere else,
    // e.g. C:\laragon\www\SmartRoad\SmartRoad\uploads\ would be __DIR__ . '/../uploads/'
    if (!empty($report['photo'])) {
        $photoPath = __DIR__ . '/uploads/' . $report['photo'];
        if (file_exists($photoPath)) {
            unlink($photoPath);
        }
    }

    echo json_encode(['success' => true]);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Could not delete report: ' . $e->getMessage()]);
}