<?php
/**
 * SmartRoad Admin — Full hazard report editor.
 * POST multipart/form-data: id, hazard_type, description, latitude,
 * longitude, status, photo (optional replacement).
 * Keeps the existing photo when no new file is uploaded; deletes the old
 * file from disk when it is replaced.
 */

require_once __DIR__ . '/db.php';
require_once __DIR__ . '/api/_helpers.php';
require_once 'auth.php';

header('Content-Type: application/json');

const EDIT_UPLOAD_DIR = __DIR__ . '/uploads/';

function editRespond(bool $success, string $message): void {
    echo json_encode(['success' => $success, 'message' => $message]);
    exit;
}

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    editRespond(false, 'Method not allowed.');
}

$id          = isset($_POST['id']) ? trim($_POST['id']) : '';
$hazardType  = isset($_POST['hazard_type']) ? trim($_POST['hazard_type']) : '';
$description = isset($_POST['description']) ? trim($_POST['description']) : '';
$latitude    = isset($_POST['latitude']) ? trim($_POST['latitude']) : '';
$longitude   = isset($_POST['longitude']) ? trim($_POST['longitude']) : '';
$status      = isset($_POST['status']) ? trim($_POST['status']) : '';

if ($id === '' || !ctype_digit($id)) {
    http_response_code(400);
    editRespond(false, 'Invalid report id.');
}
if ($hazardType === '' || $description === '' || $latitude === '' || $longitude === '' || $status === '') {
    http_response_code(400);
    editRespond(false, 'All fields are required.');
}

$hazardType = HAZARD_TYPE_ALIASES[$hazardType] ?? $hazardType;
if (!in_array($hazardType, ALLOWED_HAZARD_TYPES, true)) {
    http_response_code(400);
    editRespond(false, 'Invalid hazard type.');
}
if (!in_array($status, ALLOWED_STATUSES, true)) {
    http_response_code(400);
    editRespond(false, 'Invalid status.');
}
if (!is_numeric($latitude) || !is_numeric($longitude)
        || (float) $latitude < -90 || (float) $latitude > 90
        || (float) $longitude < -180 || (float) $longitude > 180) {
    http_response_code(400);
    editRespond(false, 'Invalid GPS coordinates.');
}

try {
    $stmt = $pdo->prepare('SELECT photo FROM hazard_reports WHERE id = ?');
    $stmt->execute([$id]);
    $existing = $stmt->fetch();
    if (!$existing) {
        http_response_code(404);
        editRespond(false, 'Report not found.');
    }
} catch (PDOException $e) {
    http_response_code(500);
    editRespond(false, 'Server error. Please try again later.');
}

// ----- Photo (optional replacement) -----
// Keep the existing filename unless a new file was actually uploaded.
$photoFilename = $existing['photo'];
$oldPhotoToDelete = null;
if (!empty($_FILES['photo']['name'])) {
    $upload = validateAndStoreImage($_FILES['photo'], EDIT_UPLOAD_DIR, 'hz_');
    if (!$upload['ok']) {
        http_response_code(400);
        editRespond(false, $upload['error']);
    }
    $oldPhotoToDelete = $existing['photo'];
    $photoFilename = $upload['filename'];
}

try {
    $stmt = $pdo->prepare('
        UPDATE hazard_reports
        SET hazard_type = ?, description = ?, latitude = ?, longitude = ?, status = ?, photo = ?
        WHERE id = ?
    ');
    $stmt->execute([$hazardType, $description, $latitude, $longitude, $status, $photoFilename, $id]);

    // Only remove the old file once the DB row referencing it has been
    // successfully updated to point at the new one.
    if ($oldPhotoToDelete !== null && file_exists(EDIT_UPLOAD_DIR . $oldPhotoToDelete)) {
        unlink(EDIT_UPLOAD_DIR . $oldPhotoToDelete);
    }

    editRespond(true, 'Report updated successfully.');
} catch (PDOException $e) {
    if ($oldPhotoToDelete !== null && $photoFilename !== $existing['photo']
            && file_exists(EDIT_UPLOAD_DIR . $photoFilename)) {
        unlink(EDIT_UPLOAD_DIR . $photoFilename);
    }
    http_response_code(500);
    editRespond(false, 'Server error. Please try again later.');
}
