<?php
/**
 * SmartRoad — Change the logged-in mobile user's password.
 * POST (form-urlencoded): user_id, current_password, new_password, confirm_password
 */

header('Content-Type: application/json');
require_once __DIR__ . '/../db.php';
require_once __DIR__ . '/_helpers.php';

const MIN_PASSWORD_LENGTH = 6;

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    apiRespond(405, 'error', ['message' => 'Method not allowed.']);
}

$userId          = isset($_POST['user_id']) ? trim($_POST['user_id']) : '';
$currentPassword = isset($_POST['current_password']) ? trim($_POST['current_password']) : '';
$newPassword     = isset($_POST['new_password']) ? trim($_POST['new_password']) : '';
$confirmPassword = isset($_POST['confirm_password']) ? trim($_POST['confirm_password']) : '';

if ($userId === '' || $currentPassword === '' || $newPassword === '' || $confirmPassword === '') {
    apiRespond(400, 'error', ['message' => 'All fields are required.']);
}
if (!ctype_digit($userId)) {
    apiRespond(400, 'error', ['message' => 'Invalid user.']);
}
if ($newPassword !== $confirmPassword) {
    apiRespond(400, 'error', ['message' => 'New password and confirmation do not match.']);
}
if (strlen($newPassword) < MIN_PASSWORD_LENGTH) {
    apiRespond(400, 'error', ['message' => 'New password must be at least ' . MIN_PASSWORD_LENGTH . ' characters.']);
}

try {
    $stmt = $pdo->prepare('SELECT password FROM users WHERE id = ?');
    $stmt->execute([$userId]);
    $user = $stmt->fetch();

    if (!$user) {
        apiRespond(400, 'error', ['message' => 'Invalid user.']);
    }
    if (!password_verify($currentPassword, $user['password'])) {
        apiRespond(401, 'error', ['message' => 'Current password is incorrect.']);
    }
    if (password_verify($newPassword, $user['password'])) {
        apiRespond(400, 'error', ['message' => 'New password must be different from the current password.']);
    }

    $newHash = password_hash($newPassword, PASSWORD_DEFAULT);
    $stmt = $pdo->prepare('UPDATE users SET password = ? WHERE id = ?');
    $stmt->execute([$newHash, $userId]);

    apiRespond(200, 'success', ['message' => 'Password changed successfully.']);
} catch (PDOException $e) {
    apiRespond(500, 'error', ['message' => 'Server error. Please try again later.']);
}
