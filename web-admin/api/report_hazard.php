<?php
/**
 * SmartRoad — Submit a hazard report from the mobile app.
 * POST multipart/form-data: user_id, hazard_type, description, latitude, longitude, photo (optional)
 */

header('Content-Type: application/json');
require_once __DIR__ . '/../db.php';
require_once __DIR__ . '/_helpers.php';

const UPLOAD_DIR = __DIR__ . '/../uploads/';

function respond(int $httpCode, string $status, ?string $id, string $message): void {
    apiRespond($httpCode, $status, ['id' => $id, 'message' => $message]);
}

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    respond(405, 'error', null, 'Method not allowed.');
}

// ----- Required fields -----
$userId      = isset($_POST['user_id']) ? trim($_POST['user_id']) : '';
$hazardType  = isset($_POST['hazard_type']) ? trim($_POST['hazard_type']) : '';
$description = isset($_POST['description']) ? trim($_POST['description']) : '';
$latitude    = isset($_POST['latitude']) ? trim($_POST['latitude']) : '';
$longitude   = isset($_POST['longitude']) ? trim($_POST['longitude']) : '';

if ($userId === '' || $hazardType === '' || $description === '' || $latitude === '' || $longitude === '') {
    respond(400, 'error', null, 'user_id, hazard_type, description, latitude and longitude are required.');
}

if (!ctype_digit($userId)) {
    respond(400, 'error', null, 'Invalid user.');
}

$hazardType = HAZARD_TYPE_ALIASES[$hazardType] ?? $hazardType;
if (!in_array($hazardType, ALLOWED_HAZARD_TYPES, true)) {
    respond(400, 'error', null, 'Invalid hazard type.');
}

if (!is_numeric($latitude) || !is_numeric($longitude)
        || (float) $latitude < -90 || (float) $latitude > 90
        || (float) $longitude < -180 || (float) $longitude > 180) {
    respond(400, 'error', null, 'Invalid GPS coordinates.');
}

// hazard_reports.user_id has a foreign key to users.id — checking here
// first gives a clean 400 instead of letting an invalid id fall through
// to a raw FK-constraint PDOException during the INSERT below.
try {
    $stmt = $pdo->prepare('SELECT id FROM users WHERE id = ?');
    $stmt->execute([$userId]);
    if (!$stmt->fetch()) {
        respond(400, 'error', null, 'Invalid user.');
    }
} catch (PDOException $e) {
    respond(500, 'error', null, 'Server error. Please try again later.');
}

// ----- Photo (optional) -----
$photoFilename = null;
if (!empty($_FILES['photo']['name'])) {
    $upload = validateAndStoreImage($_FILES['photo'], UPLOAD_DIR, 'hz_');
    if (!$upload['ok']) {
        respond(400, 'error', null, $upload['error']);
    }
    $photoFilename = $upload['filename'];
}

// ----- Insert -----
try {
    $stmt = $pdo->prepare('
        INSERT INTO hazard_reports
            (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent)
        VALUES
            (:user_id, :hazard_type, :description, :photo, :latitude, :longitude, :status, :user_agent)
    ');
    $stmt->execute([
        ':user_id'     => $userId,
        ':hazard_type' => $hazardType,
        ':description' => $description,
        ':photo'       => $photoFilename,
        ':latitude'    => $latitude,
        ':longitude'   => $longitude,
        ':status'      => 'New',
        ':user_agent'  => $_SERVER['HTTP_USER_AGENT'] ?? null,
    ]);

    respond(200, 'success', (string) $pdo->lastInsertId(), 'Hazard report submitted successfully.');
} catch (PDOException $e) {
    if ($photoFilename !== null && file_exists(UPLOAD_DIR . $photoFilename)) {
        unlink(UPLOAD_DIR . $photoFilename);
    }
    respond(500, 'error', null, 'Server error. Please try again later.');
}
