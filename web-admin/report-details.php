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
                    <td id="reportID"></td>
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
                        <img id="reportPhoto"
                             src=""
                             width="350">
                    </td>
                </tr>

            </table>

            <br>

            <button class="btn btn-warning"
                    onclick="changeStatus('Investigating')">
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

let reports = [

{ id:101,user:"Ali Ahmad",date:"2026-06-02T14:30",hazard:"Flood",lat:2.3115,lng:102.3218,status:"New",photo:"https://placehold.co/600x400?text=Flood"},

{ id:102,user:"Abu Bakar",date:"2026-06-02T09:12",hazard:"Pothole",lat:2.3120,lng:102.3225,status:"Resolved",photo:"https://placehold.co/600x400?text=Pothole"},

{ id:103,user:"Siti Noor",date:"2026-06-03T18:05",hazard:"Accident",lat:2.3140,lng:102.3260,status:"Investigating",photo:"https://placehold.co/600x400?text=Accident"},

{ id:104,user:"Ravi Kumar",date:"2026-06-04T07:45",hazard:"Fallen Tree",lat:2.3090,lng:102.3200,status:"New",photo:"https://placehold.co/600x400?text=Tree"},

{ id:105,user:"Wei Ling",date:"2026-06-04T20:15",hazard:"Pothole",lat:2.3105,lng:102.3230,status:"Resolved",photo:"https://placehold.co/600x400?text=Pothole"},

{ id:106,user:"Ali Ahmad",date:"2026-06-05T11:00",hazard:"Broken Traffic Light",lat:2.3130,lng:102.3240,status:"Investigating",photo:"https://placehold.co/600x400?text=Traffic+Light"}

];

const params = new URLSearchParams(window.location.search);

const id = parseInt(params.get("id"));

const report = reports.find(r => r.id === id);

if(report){

document.getElementById("reportID").innerHTML = report.id;

document.getElementById("reportUser").innerHTML = report.user;

document.getElementById("reportDate").innerHTML =
new Date(report.date).toLocaleString();

document.getElementById("reportHazard").innerHTML = report.hazard;

document.getElementById("reportLat").innerHTML = report.lat;

document.getElementById("reportLng").innerHTML = report.lng;

document.getElementById("reportStatus").innerHTML = report.status;

document.getElementById("reportPhoto").src = report.photo;

}

function changeStatus(status){

report.status = status;

document.getElementById("reportStatus").innerHTML = status;

alert("Status updated to " + status);

}

</script>

</body>
</html>