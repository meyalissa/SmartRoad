<?php
/**
 * SmartRoad Admin — Login page authenticating against admin_users.
 */

session_start();

if (!empty($_SESSION['admin_id'])) {
    header('Location: dashboard.php');
    exit;
}

require_once 'db.php';

$error = '';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $username = trim($_POST['username'] ?? '');
    $password = $_POST['password'] ?? '';

    if ($username === '' || $password === '') {
        $error = 'Please enter both username and password.';
    } else {
        $stmt = $pdo->prepare('SELECT id, username, password, full_name FROM admin_users WHERE username = ?');
        $stmt->execute([$username]);
        $admin = $stmt->fetch();

        if ($admin && password_verify($password, $admin['password'])) {
            session_regenerate_id(true);
            $_SESSION['admin_id']   = $admin['id'];
            $_SESSION['admin_name'] = $admin['full_name'];

            header('Location: dashboard.php');
            exit;
        } else {
            $error = 'Invalid username or password.';
        }
    }
}
?>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>SmartRoad Admin — Login</title>
<link rel="stylesheet" href="css/style.css">
</head>
<body>

<div style="max-width:340px;margin:100px auto;text-align:center">

    <div style="color:var(--primary-yellow);font-size:20px;font-weight:600;margin-bottom:4px">
        SmartRoad
    </div>
    <div class="text-muted mb-16" style="font-size:13px">
        Admin Panel
    </div>

    <form class="card" style="text-align:left" method="POST" action="login.php">

        <?php if ($error): ?>
            <div class="form-error show" style="margin-bottom:14px"><?= htmlspecialchars($error) ?></div>
        <?php endif; ?>

        <div class="form-group">
            <label class="form-label">Username</label>
            <input class="input" type="text" name="username" required autofocus
                   value="<?= htmlspecialchars($_POST['username'] ?? '') ?>">
        </div>

        <div class="form-group">
            <label class="form-label">Password</label>
            <input class="input" type="password" name="password" required>
        </div>

        <button type="submit" class="btn btn-primary" style="width:100%">
            Log In
        </button>

    </form>

</div>

</body>
</html>