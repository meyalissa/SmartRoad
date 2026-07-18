<?php
/**
 * SmartRoad — Sidebar navigation shared across the dashboard, manage-report,
 * and hazard-form pages. Expects $active_page to identify the current page
 * and relies on auth.php having already run so $_SESSION['admin_name'] and
 * $_SESSION['admin_id'] are available. Icons are provided by Bootstrap Icons.
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
        <!-- <a href="hazard-form.php" class="<?= $active_page === 'hazard-form' ? 'active' : '' ?>">
            <i class="bi bi-exclamation-triangle"></i> Add Hazard
        </a> -->
    </nav>

    <div class="sidebar-logout">
        <a href="logout.php">
            <i class="bi bi-box-arrow-right"></i> Logout
        </a>
    </div>
</aside>