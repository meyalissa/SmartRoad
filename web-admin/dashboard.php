<?php
/**
 * SmartRoad Admin — Dashboard page displaying hazard and maintenance
 * statistics alongside the most recently submitted reports.
 */

require_once 'auth.php';
require_once 'db.php';

$active_page = 'dashboard';

// Stat cards
$totalUsers    = $pdo->query('SELECT COUNT(*) FROM users')->fetchColumn();
$totalReports  = $pdo->query('SELECT COUNT(*) FROM hazard_reports')->fetchColumn();
$openReports   = $pdo->query("SELECT COUNT(*) FROM hazard_reports WHERE status != 'Resolved'")->fetchColumn();
$resolvedCount = $pdo->query("SELECT COUNT(*) FROM hazard_reports WHERE status = 'Resolved'")->fetchColumn();

// Recent reports 
$recent = $pdo->query("
    SELECT hr.id, hr.hazard_type, hr.status, hr.reported_at, u.full_name, u.profile_picture
    FROM hazard_reports hr
    JOIN users u ON u.id = hr.user_id
    ORDER BY hr.reported_at DESC
    LIMIT 5
")->fetchAll();

function initials($name) {
    $parts = preg_split('/\s+/', trim($name));
    $letters = array_map(fn($p) => mb_strtoupper(mb_substr($p, 0, 1)), array_slice($parts, 0, 2));
    return implode('', $letters);
}

function statusDot($status) {
    return match ($status) {
        'New'                 => 'dot-new',
        'Under Investigation' => 'dot-investigating',
        'Resolved'            => 'dot-resolved',
        default               => 'dot-new',
    };
}
function statusBadge($status) {
    return match ($status) {
        'New'                 => 'badge-new',
        'Under Investigation' => 'badge-investigating',
        'Resolved'            => 'badge-resolved',
        default               => 'badge-new',
    };
}
?>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>SmartRoad Admin — Dashboard</title>
<link rel="stylesheet" href="css/style.css">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
</head>
<body>

<div class="admin-layout">

    <?php $active_page = 'dashboard'; require 'sidebar.php'; ?>

    <main class="page-content">

        <div class="flex-between mb-16">
            <div class="page-title" style="margin-bottom:0">Dashboard</div>
            <div class="text-muted" style="font-size:13px">
                Welcome, <?= htmlspecialchars($_SESSION['admin_name']) ?>
            </div>
        </div>

        <div class="card">
            <div class="card-title">Overview</div>
            <div class="stat-grid">
                <div class="stat-card">
                    <div class="stat-label">Total Users</div>
                    <div class="stat-value"><?= number_format($totalUsers) ?></div>
                </div>
                <div class="stat-card border-accent">
                    <div class="stat-label">Total Reports</div>
                    <div class="stat-value accent"><?= number_format($totalReports) ?></div>
                </div>
                <div class="stat-card border-accent">
                    <div class="stat-label"><span class="dot dot-new"></span> Open Reports</div>
                    <div class="stat-value"><?= number_format($openReports) ?></div>
                </div>
                <div class="stat-card border-success">
                    <div class="stat-label"><span class="dot dot-resolved"></span> Resolved Reports</div>
                    <div class="stat-value success"><?= number_format($resolvedCount) ?></div>
                </div>
            </div>
        </div>

        <div class="card">
            <div class="flex-between mb-16">
                <div class="card-title" style="margin-bottom:0">Recent Reports</div>
                <a href="manage-report.php" class="text-accent" style="font-size:13px;text-decoration:none">View all &rarr;</a>
            </div>

            <?php if (count($recent) === 0): ?>
                <div class="empty-state">
                    <span class="icon">📭</span>
                    No hazard reports yet.
                </div>
            <?php else: ?>
                <table class="table">
                    <thead>
                        <tr>
                            <th>User</th>
                            <th>Hazard</th>
                            <th>Date</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <?php foreach ($recent as $r): ?>
                        <tr>
                            <td>
                                <div class="user-cell">
                                    <?php if (!empty($r['profile_picture'])): ?>
                                        <img src="uploads/users/<?= htmlspecialchars($r['profile_picture']) ?>"
                                             alt="<?= htmlspecialchars($r['full_name']) ?>"
                                             class="avatar-circle" style="object-fit:cover">
                                    <?php else: ?>
                                        <span class="avatar-circle"><?= htmlspecialchars(initials($r['full_name'])) ?></span>
                                    <?php endif; ?>
                                    <?= htmlspecialchars($r['full_name']) ?>
                                </div>
                            </td>
                            <td><?= htmlspecialchars($r['hazard_type']) ?></td>
                            <td><?= date('j/n', strtotime($r['reported_at'])) ?></td>
                            <td>
                                <span class="badge <?= statusBadge($r['status']) ?>">
                                    <span class="dot <?= statusDot($r['status']) ?>"></span>
                                    <?= htmlspecialchars($r['status']) ?>
                                </span>
                            </td>
                        </tr>
                        <?php endforeach; ?>
                    </tbody>
                </table>
            <?php endif; ?>
        </div>

    </main>

</div>

</body>
</html>

