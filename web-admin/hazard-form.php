<?php
  require_once 'auth.php';
?>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>SmartRoad Admin - Add Hazard</title>

<link rel="stylesheet" href="css/style.css">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons-webfont@2.44.0/tabler-icons.min.css">

</head>

<body>

<div class="admin-layout">

    <aside class="sidebar">
        <div class="sidebar-logo">SmartRoad</div>

        <nav class="sidebar-nav">
            <a href="dashboard.php">Dashboard</a>
            <a href="manage-report.php">Manage Reports</a>
            <a href="hazard-form.php" class="active">Add Hazard</a>
            <a href="logout.php">Logout</a>
        </nav>
    </aside>

    <main class="page-content">

        <div class="page-title">
            Add New Hazard
        </div>

        <div class="card">

            <form id="hazardForm">

                <div class="form-group">
                    <label>Hazard Type</label>

                    <select class="input" id="hazardType" required>
                        <option value="">Select Hazard</option>
                        <option>Pothole</option>
                        <option>Flood</option>
                        <option>Accident</option>
                        <option>Fallen Tree</option>
                        <option>Damaged Sign</option>
                        <option>Broken Traffic Light</option>
                    </select>
                </div>

                <br>

                <div class="form-group">
                    <label>Description</label>

                    <textarea
                        class="input"
                        id="description"
                        rows="4"
                        placeholder="Enter hazard description..."
                        required></textarea>
                </div>

                <br>

                <div class="form-group">
                    <label>Latitude</label>

                    <input
                        class="input"
                        type="number"
                        step="0.000001"
                        id="latitude"
                        placeholder="e.g. 2.3115"
                        required>
                </div>

                <br>

                <div class="form-group">
                    <label>Longitude</label>

                    <input
                        class="input"
                        type="number"
                        step="0.000001"
                        id="longitude"
                        placeholder="e.g. 102.3218"
                        required>
                </div>

                <br>

                <div class="form-group">
                    <label>Upload Photo</label>

                    <input
                        class="input"
                        type="file"
                        id="photo"
                        accept="image/*">
                </div>

                <br>

                <div class="form-group">
                    <label>Status</label>

                    <select class="input" id="status">

                        <option>New</option>

                        <option>Under Investigation</option>

                        <option>Resolved</option>

                    </select>
                </div>

                <br>

                <button
                    type="submit"
                    class="btn btn-primary">

                    <i class="ti ti-device-floppy"></i>

                    Save Hazard

                </button>

                <button
                    type="reset"
                    class="btn btn-outline">

                    Reset

                </button>

            </form>

        </div>

    </main>

</div>

<script>

document.getElementById("hazardForm").addEventListener("submit", function(e){

    e.preventDefault();

    alert("Hazard added successfully!");

    this.reset();

});

</script>

</body>
</html>