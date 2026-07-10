<?php
/**
 * SmartRoad — Sidebar nav (shared across dashboard, manage-report, hazard-form)
 * Place this in web-admin/.
 * Before including it, set: $active_page = 'dashboard'; (or 'manage-report', 'hazard-form')
 * Requires auth.php to have already run so $_SESSION['admin_name'] exists.
 *
 * Uses Bootstrap Icons (CDN). Make sure this line is in the <head> of every
 * page that includes this sidebar:
 * <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
 */
?>
<aside class="sidebar">
    <div class="sidebar-logo">
        <img src="uploads/logo.png" alt="SmartRoad logo" class="sidebar-logo-img"> SmartRoad
    </div>

    <nav class="sidebar-nav">
        <a href="dashboard.php" class="<?= $active_page === 'dashboard' ? 'active' : '' ?>">
            <i class="bi bi-speedometer2"></i> Dashboard
        </a>
        <a href="manage-report.php" class="<?= $active_page === 'manage-report' ? 'active' : '' ?>">
            <i class="bi bi-list-check"></i> Manage Reports
        </a>
        <a href="hazard-form.php" class="<?= $active_page === 'hazard-form' ? 'active' : '' ?>">
            <i class="bi bi-exclamation-triangle"></i> Add Hazard
        </a>
    </nav>

    <div class="sidebar-logout">
        <a href="logout.php">
            <i class="bi bi-box-arrow-right"></i> Logout
        </a>
    </div>
</aside>