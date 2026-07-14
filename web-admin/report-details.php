<?php
require_once 'auth.php';
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>SmartRoad Admin - Report Details</title>
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons-webfont@2.44.0/tabler-icons.min.css">
    <link class="input" rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
    <link rel="stylesheet" href="https://unpkg.com/leaflet/dist/leaflet.css"/>
    <script src="https://unpkg.com/leaflet/dist/leaflet.js"></script>
</head>
<body>

    <div class="admin-layout">
        <?php $active_page = 'hazard-form'; require 'sidebar.php'; ?>

        <main class="page-content">
            <div style="margin-bottom:20px;">
                <button class="back-btn" onclick="history.back()">
                    <i class="ti ti-arrow-left"></i> Back
                </button>
            </div>

            <div class="page-title">Report Details</div>

            <div class="card">
                <table class="table">
                    <tr>
                        <th width="220">Report ID</th>
                        <td id="reportID">Loading...</td>
                    </tr>
                    <tr>
                        <th>Reported By</th>
                        <td id="reportUser"></td>
                    </tr>
                    <tr>
                        <th>Date &amp; Time</th>
                        <td id="reportDate"></td>
                    </tr>
                </table>

                <form id="editForm">
                    <div class="form-group">
                        <label class="form-label">Hazard Type</label>
                        <select class="select" id="editHazardType" required>
                            <option value="Pothole">Pothole</option>
                            <option value="Flood">Flood</option>
                            <option value="Accident">Accident</option>
                            <option value="Fallen Tree">Fallen Tree</option>
                            <option value="Damaged Road Sign">Damaged Road Sign</option>
                            <option value="Broken Traffic Light">Broken Traffic Light</option>
                        </select>
                        <div class="form-error" id="errHazardType">Please select a hazard type.</div>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Description</label>
                        <textarea class="textarea" id="editDescription" rows="4" required></textarea>
                        <div class="form-error" id="errDescription">Please enter a description.</div>
                    </div>

                    <div class="form-group flex gap-12">
                        <div style="flex:1">
                            <label class="form-label">Latitude</label>
                            <input class="input" type="number" step="0.0000001" id="editLatitude" required>
                            <div class="form-error" id="errLatitude">Must be between -90 and 90.</div>
                        </div>
                        <div style="flex:1">
                            <label class="form-label">Longitude</label>
                            <input class="input" type="number" step="0.0000001" id="editLongitude" required>
                            <div class="form-error" id="errLongitude">Must be between -180 and 180.</div>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Status</label>
                        <select class="select" id="editStatus" required>
                            <option value="New">New</option>
                            <option value="Under Investigation">Under Investigation</option>
                            <option value="Resolved">Resolved</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Photo</label>
                        <div class="photo-box" style="width:160px;height:120px;margin-bottom:8px;">
                            <img id="reportPhoto" src="" alt="Report photo" style="display:none;">
                        </div>
                        <input class="input" type="file" id="editPhoto" accept="image/jpeg,image/png,image/webp">
                        <div class="form-error" id="errPhoto"></div>
                    </div>

                    <div class="location-info">
                        <span id="reportCoords">Loading...</span>
                    </div>
                    <div id="map"></div>

                    <div class="action-buttons">
                        <button type="submit" class="btn btn-primary" id="saveReportBtn">
                            <i class="ti ti-device-floppy"></i> Save Changes
                        </button>
                    </div>
                </form>
            </div>

            <!-- ============ MAINTENANCE INFORMATION ============ -->
            <div class="card">
                <div class="card-title">Maintenance Information</div>

                <form id="maintenanceForm">
                    <div class="form-group">
                        <label class="form-label">Maintenance Team</label>
                        <input class="input" type="text" id="maintenanceTeam" placeholder="e.g. Public Works Crew 3">
                    </div>

                    <div class="form-group flex gap-12">
                        <div style="flex:1">
                            <label class="form-label">Repair Date</label>
                            <input class="input" type="date" id="repairDate">
                        </div>
                        <div style="flex:1">
                            <label class="form-label">Completed Date</label>
                            <input class="input" type="date" id="completedDate">
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Maintenance Notes</label>
                        <textarea class="textarea" id="maintenanceNotes" rows="3" placeholder="Work performed, materials used, follow-up needed…"></textarea>
                    </div>

                    <div class="action-buttons">
                        <button type="submit" class="btn btn-primary" id="saveMaintenanceBtn">
                            <i class="ti ti-device-floppy"></i> Save
                        </button>
                    </div>
                </form>
            </div>
        </main>
    </div>

    <script>
        const params = new URLSearchParams(window.location.search);
        const reportId = Number(params.get("id"));
        let report = null;
        let map = null;
        let marker = null;

        async function loadReport() {
            try {
                const response = await fetch("get_report.php");
                const reports = await response.json();

                report = reports.find(r => Number(r.id) === reportId);

                if (!report) {
                    alert("Report not found");
                    return;
                }

                document.getElementById("reportID").textContent = report.id;
                document.getElementById("reportUser").textContent = report.user;
                document.getElementById("reportDate").textContent = new Date(report.date).toLocaleString();

                document.getElementById("editHazardType").value = report.hazard;
                document.getElementById("editDescription").value = report.description || "";
                document.getElementById("editStatus").value = report.status;

                const lat = Number(report.lat);
                const lng = Number(report.lng);

                document.getElementById("editLatitude").value = lat;
                document.getElementById("editLongitude").value = lng;
                document.getElementById("reportCoords").textContent = `${lat.toFixed(6)}, ${lng.toFixed(6)}`;

                const photoImg = document.getElementById("reportPhoto");

                if (report.photo) {
                    photoImg.src = "uploads/" + report.photo;
                    photoImg.style.display = "block";
                } else {
                    photoImg.src = "";
                    photoImg.style.display = "none";
                }


                // Maintenance section
                document.getElementById("maintenanceTeam").value = report.maintenance_team || "";
                document.getElementById("maintenanceNotes").value = report.maintenance_notes || "";
                document.getElementById("repairDate").value = report.repair_date 
                    ? report.repair_date.substring(0, 10) 
                    : "";

                document.getElementById("completedDate").value = report.completed_date 
                    ? report.completed_date.substring(0, 10) 
                    : "";


                // ================================
                // FIX LEAFLET MAP REINITIALIZATION
                // ================================
                if (map) {
                    map.remove();
                    map = null;
                    marker = null;
                }


                map = L.map('map', { zoomControl: false }).setView([lat, lng], 15);

                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    attribution: '© OpenStreetMap contributors'
                }).addTo(map);

                marker = L.marker([lat, lng]).addTo(map);


                // Ensure Leaflet recalculates correctly
                setTimeout(() => {
                    map.invalidateSize();
                }, 200);


            } catch (error) {
                console.error(error);
                alert("Failed loading report");
            }
        }

        function clearErrors(form) {
            form.querySelectorAll('.form-error').forEach(el => el.classList.remove('show'));
            form.querySelectorAll('.invalid').forEach(el => el.classList.remove('invalid'));
        }

        function showFieldError(fieldEl, errorEl) {
            fieldEl.classList.add('invalid');
            errorEl.classList.add('show');
        }

        document.getElementById("editForm").addEventListener("submit", async (e) => {
            e.preventDefault();
            if (!report) return;
            clearErrors(e.target);

            const hazardType = document.getElementById("editHazardType").value;
            const description = document.getElementById("editDescription").value.trim();
            const latitude = document.getElementById("editLatitude").value;
            const longitude = document.getElementById("editLongitude").value;
            const status = document.getElementById("editStatus").value;
            const photoFile = document.getElementById("editPhoto").files[0];

            let valid = true;
            if (!description) {
                showFieldError(document.getElementById("editDescription"), document.getElementById("errDescription"));
                valid = false;
            }
            const lat = Number(latitude), lng = Number(longitude);
            if (!latitude || lat < -90 || lat > 90) {
                showFieldError(document.getElementById("editLatitude"), document.getElementById("errLatitude"));
                valid = false;
            }
            if (!longitude || lng < -180 || lng > 180) {
                showFieldError(document.getElementById("editLongitude"), document.getElementById("errLongitude"));
                valid = false;
            }
            if (!valid) return;

            const btn = document.getElementById("saveReportBtn");
            btn.disabled = true;
            btn.textContent = "Saving...";

            try {
                const formData = new FormData();
                formData.append("id", report.id);
                formData.append("hazard_type", hazardType);
                formData.append("description", description);
                formData.append("latitude", latitude);
                formData.append("longitude", longitude);
                formData.append("status", status);
                if (photoFile) formData.append("photo", photoFile);

                const response = await fetch("edit_report.php", { method: "POST", body: formData });
                const result = await response.json();

                if (!result.success) {
                    document.getElementById("errPhoto").textContent = result.message || "Update failed.";
                    document.getElementById("errPhoto").classList.add("show");
                    return;
                }

                alert("Report updated successfully.");
                window.location.href = "manage-report.php";
            } catch (error) {
                console.error(error);
                alert("Server error while saving the report.");
            } finally {
                btn.disabled = false;
                btn.innerHTML = '<i class="ti ti-device-floppy"></i> Save Changes';
            }
        });

        document.getElementById("maintenanceForm").addEventListener("submit", async (e) => {
            e.preventDefault();
            if (!report) return;

            const btn = document.getElementById("saveMaintenanceBtn");
            btn.disabled = true;
            btn.textContent = "Saving...";

            try {
                const formData = new FormData();
                formData.append("hazard_report_id", report.id);
                formData.append("maintenance_team", document.getElementById("maintenanceTeam").value.trim());
                formData.append("repair_date", document.getElementById("repairDate").value);
                formData.append("completed_date", document.getElementById("completedDate").value);
                formData.append("maintenance_notes", document.getElementById("maintenanceNotes").value.trim());

                const response = await fetch("save_maintenance.php", { method: "POST", body: formData });
                const result = await response.json();

                if (!result.success) {
                    alert(result.message || "Failed saving maintenance information.");
                    return;
                }
                alert("Maintenance information saved.");
            } catch (error) {
                console.error(error);
                alert("Server error while saving maintenance information.");
            } finally {
                btn.disabled = false;
                btn.innerHTML = '<i class="ti ti-device-floppy"></i> Save';
            }
        });

        loadReport();
    </script>

</body>
</html>
