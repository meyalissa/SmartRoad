<?php
require_once 'db.php';

$message = "";

if ($_SERVER["REQUEST_METHOD"] === "POST") {

    $hazard_type = $_POST['hazard_type'] ?? '';
    $description = $_POST['description'] ?? '';
    $latitude    = $_POST['latitude'] ?? '';
    $longitude   = $_POST['longitude'] ?? '';
    $status      = $_POST['status'] ?? 'New';

    // TODO: Replace with the logged-in user's ID if available.
    $user_id = 1;

    $user_agent = $_SERVER['HTTP_USER_AGENT'];
    $photo = null;

    if (!empty($_FILES['photo']['name'])) {

        $uploadDir = "uploads/";

        if (!is_dir($uploadDir)) {
            mkdir($uploadDir, 0777, true);
        }

        $photo = time() . "_" . basename($_FILES['photo']['name']);

        move_uploaded_file(
            $_FILES['photo']['tmp_name'],
            $uploadDir . $photo
        );
    }

    $stmt = $pdo->prepare("
        INSERT INTO hazard_reports
        (user_id,hazard_type,description,photo,latitude,longitude,status,user_agent)
        VALUES
        (:user_id,:hazard_type,:description,:photo,:latitude,:longitude,:status,:user_agent)
    ");

    $stmt->execute([
        ':user_id'=>$user_id,
        ':hazard_type'=>$hazard_type,
        ':description'=>$description,
        ':photo'=>$photo,
        ':latitude'=>$latitude,
        ':longitude'=>$longitude,
        ':status'=>$status,
        ':user_agent'=>$user_agent
    ]);

    header("Location: manage-report.php");
    exit;
}
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

<div class="page-title">Add New Hazard</div>

<div class="card">

<form method="POST" enctype="multipart/form-data">

<div class="form-group">
<label>Hazard Type</label>
<select class="input" name="hazard_type" required>
<option value="">Select Hazard</option>
<option>Pothole</option>
<option>Flood</option>
<option>Accident</option>
<option>Fallen Tree</option>
<option>Damaged Road Sign</option>
<option>Broken Traffic Light</option>
</select>
</div>

<br>

<div class="form-group">
<label>Description</label>
<textarea class="input" name="description" rows="4" placeholder="Enter hazard description..." required></textarea>
</div>

<br>

<div class="form-group">
<label>Latitude</label>
<input class="input" type="number" step="0.000001" name="latitude" required>
</div>

<br>

<div class="form-group">
<label>Longitude</label>
<input class="input" type="number" step="0.000001" name="longitude" required>
</div>

<br>

<div class="form-group">
<label>Upload Photo</label>
<input class="input" type="file" name="photo" accept="image/*">
</div>

<br>

<div class="form-group">
<label>Status</label>
<select class="input" name="status">
<option>New</option>
<option>Under Investigation</option>
<option>Resolved</option>
</select>
</div>

<br>

<button type="submit" class="btn btn-primary">
<i class="ti ti-device-floppy"></i>
Save Hazard
</button>

<button type="reset" class="btn btn-outline">
Reset
</button>

</form>

</div>

</main>

</div>

</body>
</html>
