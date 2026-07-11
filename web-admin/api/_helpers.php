<?php
/**
 * SmartRoad — Shared helpers and validation constants.
 * Included by every file in web-admin/api/ (mobile JSON APIs) and also by
 * the admin-side write endpoints in web-admin/ (edit_report.php,
 * save_maintenance.php) so validation rules for hazard type/status/images
 * only live in one place.
 */

// Hosts the Android app is actually configured to call (see
// android-app/.../network/ApiClient.java: EMULATOR_BASE_URL / DEVICE_LAN_IP).
// The incoming Host header is checked against this allowlist before it is
// echoed back into any generated URL (e.g. photo links), so a spoofed Host
// header can never redirect API-returned asset URLs to another domain.
const TRUSTED_API_HOSTS = ['10.0.2.2', '10.82.146.84', 'localhost', '127.0.0.1'];

/** Builds "http(s)://<trusted-host>/SmartRoad/web-admin/" for the current request. */
function apiOrigin(): string {
    $host = $_SERVER['HTTP_HOST'] ?? '';
    $hostOnly = explode(':', $host)[0];
    if (!in_array($hostOnly, TRUSTED_API_HOSTS, true)) {
        $host = TRUSTED_API_HOSTS[0];
    }
    $scheme = (!empty($_SERVER['HTTPS']) && $_SERVER['HTTPS'] !== 'off') ? 'https' : 'http';
    return $scheme . '://' . $host . '/SmartRoad/web-admin/';
}

/** Sends a uniform JSON envelope and stops execution. Used by every mobile API. */
function apiRespond(int $httpCode, string $status, array $extra = []): void {
    http_response_code($httpCode);
    echo json_encode(array_merge(['status' => $status], $extra));
    exit;
}

const MAX_IMAGE_UPLOAD_BYTES = 8 * 1024 * 1024; // 8 MB safety net; the app compresses before upload
const ALLOWED_IMAGE_MIME_TO_EXT = [
    'image/jpeg' => 'jpg',
    'image/png'  => 'png',
    'image/webp' => 'webp',
];

// Must match the hazard_type / status ENUMs in hazard_reports (see database.sql).
const ALLOWED_HAZARD_TYPES = [
    'Pothole', 'Flood', 'Accident', 'Fallen Tree', 'Damaged Road Sign', 'Broken Traffic Light',
];
// Maps legacy/alternate labels from older app builds onto the DB's ENUM values.
const HAZARD_TYPE_ALIASES = [
    'Traffic Accident' => 'Accident',
];
const ALLOWED_STATUSES = ['New', 'Under Investigation', 'Resolved'];

/**
 * Validates one $_FILES[...] entry as a real image (content-sniffed, not by
 * filename/extension) and moves it into $targetDir under a generated,
 * collision-proof name. Shared by report_hazard.php and update_profile.php
 * so the upload-security rules only live in one place.
 *
 * @return array{ok: bool, filename: ?string, error: ?string}
 */
function validateAndStoreImage(array $file, string $targetDir, string $filenamePrefix): array {
    if ($file['error'] !== UPLOAD_ERR_OK) {
        return ['ok' => false, 'filename' => null, 'error' => 'Photo upload failed. Please try again.'];
    }
    if ($file['size'] > MAX_IMAGE_UPLOAD_BYTES) {
        return ['ok' => false, 'filename' => null, 'error' => 'Photo is too large (max 8 MB).'];
    }

    // Trust the actual file content, not the client-supplied name/extension,
    // so a renamed executable can never pass as an image.
    $finfo = finfo_open(FILEINFO_MIME_TYPE);
    $mime = finfo_file($finfo, $file['tmp_name']);
    finfo_close($finfo);

    if (!isset(ALLOWED_IMAGE_MIME_TO_EXT[$mime]) || getimagesize($file['tmp_name']) === false) {
        return ['ok' => false, 'filename' => null, 'error' => 'Photo must be a valid JPG, PNG or WEBP image.'];
    }

    if (!is_dir($targetDir)) {
        mkdir($targetDir, 0777, true);
    }

    $filename = uniqid($filenamePrefix, true) . '.' . ALLOWED_IMAGE_MIME_TO_EXT[$mime];
    if (!move_uploaded_file($file['tmp_name'], $targetDir . $filename)) {
        return ['ok' => false, 'filename' => null, 'error' => 'Could not save photo. Please try again.'];
    }

    return ['ok' => true, 'filename' => $filename, 'error' => null];
}
