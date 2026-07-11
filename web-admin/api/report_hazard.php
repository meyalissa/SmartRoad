<?php
/**
 * SmartRoad — Submit a hazard report from the mobile app.
 * POST multipart/form-data: user_id, hazard_type, description, latitude, longitude, photo (optional)
 */

header('Content-Type: application/json');
require_once __DIR__ . '/../db.php';

const MAX_PHOTO_BYTES = 8 * 1024 * 1024; // 8 MB safety net (app compresses before upload)
const UPLOAD_DIR = __DIR__ . '/../uploads/';

// Maps legacy/alternate labels from older app builds onto the DB's ENUM values.
const HAZARD_TYPE_ALIASES = [
    'Traffic Accident' => 'Accident',
];
const ALLOWED_HAZARD_TYPES = [
    'Pothole', 'Flood', 'Accident', 'Fallen Tree', 'Damaged Road Sign', 'Broken Traffic Light',
];
const ALLOWED_IMAGE_MIME_TO_EXT = [
    'image/jpeg' => 'jpg',
    'image/png'  => 'png',
    'image/webp' => 'webp',
];

function respond(int $httpCode, string $status, ?string $id, string $message): void {
    http_response_code($httpCode);
    echo json_encode(['status' => $status, 'message' => $message, 'id' => $id]);
    exit;
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
    $file = $_FILES['photo'];

    if ($file['error'] !== UPLOAD_ERR_OK) {
        respond(400, 'error', null, 'Photo upload failed. Please try again.');
    }
    if ($file['size'] > MAX_PHOTO_BYTES) {
        respond(400, 'error', null, 'Photo is too large (max 8 MB).');
    }

    // Trust the actual file content, not the client-supplied name/extension,
    // so a renamed executable can never pass as an image.
    $finfo = finfo_open(FILEINFO_MIME_TYPE);
    $mime = finfo_file($finfo, $file['tmp_name']);
    finfo_close($finfo);

    if (!isset(ALLOWED_IMAGE_MIME_TO_EXT[$mime]) || getimagesize($file['tmp_name']) === false) {
        respond(400, 'error', null, 'Photo must be a valid JPG, PNG or WEBP image.');
    }

    if (!is_dir(UPLOAD_DIR)) {
        mkdir(UPLOAD_DIR, 0777, true);
    }

    $photoFilename = uniqid('hz_', true) . '.' . ALLOWED_IMAGE_MIME_TO_EXT[$mime];
    if (!move_uploaded_file($file['tmp_name'], UPLOAD_DIR . $photoFilename)) {
        respond(500, 'error', null, 'Could not save photo. Please try again.');
    }
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
