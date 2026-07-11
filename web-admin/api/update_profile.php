<?php
/**
 * SmartRoad — Update the logged-in mobile user's profile.
 * POST multipart/form-data: user_id, full_name, email, photo (optional)
 */

header('Content-Type: application/json');
require_once __DIR__ . '/../db.php';
require_once __DIR__ . '/_helpers.php';

const PROFILE_UPLOAD_DIR = __DIR__ . '/../uploads/users/';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    apiRespond(405, 'error', ['message' => 'Method not allowed.']);
}

// ----- Required fields -----
$userId   = isset($_POST['user_id']) ? trim($_POST['user_id']) : '';
$fullName = isset($_POST['full_name']) ? trim($_POST['full_name']) : '';
$email    = isset($_POST['email']) ? trim($_POST['email']) : '';

if ($userId === '' || $fullName === '' || $email === '') {
    apiRespond(400, 'error', ['message' => 'user_id, full_name and email are required.']);
}
if (!ctype_digit($userId)) {
    apiRespond(400, 'error', ['message' => 'Invalid user.']);
}
if (mb_strlen($fullName) > 100) {
    apiRespond(400, 'error', ['message' => 'Full name is too long (max 100 characters).']);
}
if (!filter_var($email, FILTER_VALIDATE_EMAIL) || strlen($email) > 100) {
    apiRespond(400, 'error', ['message' => 'Please enter a valid email address.']);
}

try {
    $stmt = $pdo->prepare('SELECT id, profile_picture FROM users WHERE id = ?');
    $stmt->execute([$userId]);
    $user = $stmt->fetch();
    if (!$user) {
        apiRespond(400, 'error', ['message' => 'Invalid user.']);
    }
} catch (PDOException $e) {
    apiRespond(500, 'error', ['message' => 'Server error. Please try again later.']);
}

// ----- Photo (optional) -----
$newPhotoFilename = null;
if (!empty($_FILES['photo']['name'])) {
    $upload = validateAndStoreImage($_FILES['photo'], PROFILE_UPLOAD_DIR, 'u_');
    if (!$upload['ok']) {
        apiRespond(400, 'error', ['message' => $upload['error']]);
    }
    $newPhotoFilename = $upload['filename'];
}

// ----- Update -----
try {
    if ($newPhotoFilename !== null) {
        $stmt = $pdo->prepare('UPDATE users SET full_name = ?, email = ?, profile_picture = ? WHERE id = ?');
        $stmt->execute([$fullName, $email, $newPhotoFilename, $userId]);

        // Clean up the previous photo now that the new one is saved and committed.
        $oldPhoto = $user['profile_picture'];
        if ($oldPhoto && file_exists(PROFILE_UPLOAD_DIR . $oldPhoto)) {
            unlink(PROFILE_UPLOAD_DIR . $oldPhoto);
        }
    } else {
        $stmt = $pdo->prepare('UPDATE users SET full_name = ?, email = ? WHERE id = ?');
        $stmt->execute([$fullName, $email, $userId]);
    }

    $photoFilename = $newPhotoFilename ?? $user['profile_picture'];
    $photoUrl = $photoFilename ? apiOrigin() . 'uploads/users/' . rawurlencode($photoFilename) : null;

    apiRespond(200, 'success', [
        'message'  => 'Profile updated successfully.',
        'fullname' => $fullName,
        'email'    => $email,
        'photo'    => $photoUrl,
    ]);
} catch (PDOException $e) {
    if ($newPhotoFilename !== null && file_exists(PROFILE_UPLOAD_DIR . $newPhotoFilename)) {
        unlink(PROFILE_UPLOAD_DIR . $newPhotoFilename);
    }
    apiRespond(500, 'error', ['message' => 'Server error. Please try again later.']);
}
