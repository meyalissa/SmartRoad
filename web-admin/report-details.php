<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>SmartRoad Admin - Report Details</title>

<link rel="stylesheet" href="css/style.css">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons-webfont@2.44.0/tabler-icons.min.css">

</head>

<body>

<div class="admin-layout">

    <aside class="sidebar">
        <div class="sidebar-logo">SmartRoad</div>

        <nav class="sidebar-nav">
            <a href="dashboard.php">Dashboard</a>
            <a href="manage-report.php" class="active">Manage Reports</a>
            <a href="hazard-form.php">Add Hazard</a>
            <a href="logout.php">Logout</a>
        </nav>
    </aside>

    <main class="page-content">

        <button class="btn" onclick="history.back()">
            <i class="ti ti-arrow-left"></i>
            Back
        </button>

        <div class="page-title">
            Report Details
        </div>

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
                    <th>Date & Time</th>
                    <td id="reportDate"></td>
                </tr>

                <tr>
                    <th>Hazard Type</th>
                    <td id="reportHazard"></td>
                </tr>

                <tr>
                    <th>Latitude</th>
                    <td id="reportLat"></td>
                </tr>

                <tr>
                    <th>Longitude</th>
                    <td id="reportLng"></td>
                </tr>

                <tr>
                    <th>Status</th>
                    <td id="reportStatus"></td>
                </tr>

                <tr>
                    <th>Photo</th>
                    <td>
                        <img
                            id="reportPhoto"
                            src=""
                            alt="Report Photo"
                            width="350"
                            style="max-width:100%;border-radius:8px;"
                        >
                    </td>
                </tr>

            </table>

            <br>

            <button class="btn btn-warning"
                    onclick="changeStatus('Under Investigation')">
                Under Investigation
            </button>

            <button class="btn btn-success"
                    onclick="changeStatus('Resolved')">
                Mark as Resolved
            </button>

        </div>

    </main>

</div>

<script>

const params = new URLSearchParams(window.location.search);
const reportId = Number(params.get("id"));

let report = null;

async function loadReport() {

    try {

        const response = await fetch("get_report.php");

        if (!response.ok) {
            throw new Error("Unable to fetch reports.");
        }

        const reports = await response.json();

        report = reports.find(r => Number(r.id) === reportId);

        if (!report) {
            alert("Report not found.");
            return;
        }

        document.getElementById("reportID").textContent = report.id;
        document.getElementById("reportUser").textContent = report.user;
        document.getElementById("reportHazard").textContent = report.hazard;
        document.getElementById("reportLat").textContent = report.lat;
        document.getElementById("reportLng").textContent = report.lng;
        document.getElementById("reportStatus").textContent = report.status;

        document.getElementById("reportDate").textContent =
            new Date(report.date).toLocaleString();

        document.getElementById("reportPhoto").src = report.photo;

    } catch (error) {

        console.error(error);
        alert("Failed to load report.");

    }

}

function changeStatus(status){

    if(!report){
        return;
    }

    report.status = status;

    document.getElementById("reportStatus").textContent = status;

    // Future:
    // fetch("update_report_status.php", {
    //     method: "POST",
    //     headers: {
    //         "Content-Type":"application/json"
    //     },
    //     body: JSON.stringify({
    //         id: report.id,
    //         status: status
    //     })
    // });

    alert("Status updated to " + status);

}

loadReport();

</script>

</body>
</html>