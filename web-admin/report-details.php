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
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

    <style>
        button:disabled {
            background: #999 !important;
            cursor: not-allowed;
            opacity: 0.6;
        }
    </style>
</head>


<body>

    <div class="admin-layout">

        <?php $active_page = 'hazard-form'; require 'sidebar.php'; ?>
    
        <main class="page-content">

            <div style="margin-bottom:20px;">
                <button class="back-btn" onclick="history.back()">
                    <i class="ti ti-arrow-left"></i>
                    Back
                </button>
            </div>


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
                                width="350"
                                style="max-width:100%;border-radius:8px;"
                            >
                        </td>
                    </tr>

                </table>


                <br>


                <button 
                    id="investigateBtn"
                    class="btn btn-warning"
                    onclick="changeStatus('Under Investigation')">

                    Under Investigation

                </button>


                <button 
                    id="resolvedBtn"
                    class="btn btn-success"
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

                const reports = await response.json();


                report = reports.find(
                    r => Number(r.id) === reportId
                );


                if (!report) {

                    alert("Report not found");

                    return;

                }


                document.getElementById("reportID").textContent = report.id;

                document.getElementById("reportUser").textContent = report.user;

                document.getElementById("reportDate").textContent =
                    new Date(report.date).toLocaleString();

                document.getElementById("reportHazard").textContent =
                    report.hazard;

                document.getElementById("reportLat").textContent =
                    report.lat;

                document.getElementById("reportLng").textContent =
                    report.lng;

                document.getElementById("reportStatus").textContent =
                    report.status;


                if (report.photo) {

                    document.getElementById("reportPhoto").src =
                        "uploads/" + report.photo;

                }


                updateButtons();


            } catch (error) {

                console.error(error);

                alert("Failed loading report");

            }

        }




        async function changeStatus(status) {

            if (!report) {

                return;

            }


            try {

                const response = await fetch(
                    "update-report-status.php",
                    {
                        method: "POST",

                        headers: {
                            "Content-Type": "application/json"
                        },

                        body: JSON.stringify({
                            id: report.id,
                            status: status
                        })
                    }
                );


                const result = await response.json();


                if (!result.success) {

                    alert("Failed updating status");

                    return;

                }


                report.status = status;


                document.getElementById("reportStatus").textContent =
                    status;


                updateButtons();


                alert(
                    "Status updated to " + status
                );


            } catch (error) {

                console.error(error);

                alert("Server error");

            }

        }





        function updateButtons() {

            const investigate =
                document.getElementById("investigateBtn");


            const resolved =
                document.getElementById("resolvedBtn");


            investigate.disabled = false;

            resolved.disabled = false;



            if (report.status === "Under Investigation") {

                investigate.disabled = true;

            }


            if (report.status === "Resolved") {

                resolved.disabled = true;

            }

        }



        loadReport();

    </script>


</body>

</html>