<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>SmartRoad Admin - Add Hazard</title>

<link rel="stylesheet" href="css/style.css">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons-webfont@2.44.0/tabler-icons.min.css">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

</head>

<body>

<div class="admin-layout">

    <?php $active_page = 'hazard-form'; require 'sidebar.php'; ?>

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