<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>SmartRoad Admin — Manage Reports</title>
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
      <a href="login.php">Logout</a>
    </nav>
  </aside>

  <main class="page-content">
    <div class="page-title">Manage Reports</div>

    <div class="card">

      <!-- ============ SEARCH + FILTER BAR ============ -->
      <div class="filter-bar">
        <input class="input" type="text" id="searchInput" placeholder="Search by user or description...">

        <select class="select" id="hazardFilter">
          <option value="">All hazard types</option>
          <option value="Pothole">Pothole</option>
          <option value="Flood">Flood</option>
          <option value="Accident">Accident</option>
          <option value="Fallen Tree">Fallen Tree</option>
          <option value="Damaged Sign">Damaged Sign</option>
          <option value="Broken Traffic Light">Broken Traffic Light</option>
        </select>

        <select class="select" id="statusFilter">
          <option value="">All statuses</option>
          <option value="New">New</option>
          <option value="Investigating">Under Investigation</option>
          <option value="Resolved">Resolved</option>
        </select>

        <input class="input" type="date" id="dateFilter" style="max-width:160px">

        <select class="select" id="sortSelect" style="max-width:150px">
          <option value="newest">Newest first</option>
          <option value="oldest">Oldest first</option>
        </select>
      </div>

      <!-- ============ TABLE ============ -->
      <table class="table" id="reportsTable">
        <thead>
          <tr>
            <th>User</th>
            <th>Date / time</th>
            <th>Hazard</th>
            <th>GPS</th>
            <th>Status</th>
            <th></th>
          </tr>
        </thead>
        <tbody id="reportsBody">
          <!-- rows injected by JS -->
        </tbody>
      </table>

      <!-- ============ EMPTY STATE ============ -->
      <div class="empty-state" id="emptyState" style="display:none">
        <i class="ti ti-search-off icon" aria-hidden="true"></i>
        No reports found for these filters.
      </div>

      <!-- ============ PAGINATION ============ -->
      <div class="pagination" id="pagination"></div>

    </div>
  </main>
</div>

<!-- ============ DELETE CONFIRM MODAL ============ -->
<div class="modal-overlay" id="deleteModal">
  <div class="modal">
    <div class="modal-title">Delete this report?</div>
    <div class="modal-text">This action cannot be undone. The report and its photo will be permanently removed.</div>
    <div class="modal-actions">
      <button class="btn btn-outline" id="cancelDelete">Cancel</button>
      <button class="btn btn-danger" id="confirmDelete">Delete</button>
    </div>
  </div>
</div>

<script>
/* ============================================================
   MOCK DATA
   Replace this block with a real fetch() call to your backend, e.g.:

   let reports = [];
   async function loadReports() {
     const res = await fetch('api/get_reports.php');
     reports = await res.json();
     renderTable();
   }
   loadReports();

   Each report object from your PHP/Node endpoint should have the
   same shape as the mock objects below.
   ============================================================ */
let reports = [
  { id: 101, user: "Ali Ahmad",  date: "2026-06-02T14:30", hazard: "Flood",   lat: 2.3115, lng: 102.3218, status: "New",           photo: "flood1.jpg" },
  { id: 102, user: "Abu Bakar",  date: "2026-06-02T09:12", hazard: "Pothole", lat: 2.3120, lng: 102.3225, status: "Resolved",       photo: "pothole1.jpg" },
  { id: 103, user: "Siti Noor",  date: "2026-06-03T18:05", hazard: "Accident",lat: 2.3140, lng: 102.3260, status: "Investigating",  photo: "accident1.jpg" },
  { id: 104, user: "Ravi Kumar", date: "2026-06-04T07:45", hazard: "Fallen Tree", lat: 2.3090, lng: 102.3200, status: "New",        photo: "tree1.jpg" },
  { id: 105, user: "Wei Ling",   date: "2026-06-04T20:15", hazard: "Pothole", lat: 2.3105, lng: 102.3230, status: "Resolved",       photo: "pothole2.jpg" },
  { id: 106, user: "Ali Ahmad",  date: "2026-06-05T11:00", hazard: "Broken Traffic Light", lat: 2.3130, lng: 102.3240, status: "Investigating", photo: "light1.jpg" },
];

const ROWS_PER_PAGE = 5;
let currentPage = 1;
let pendingDeleteId = null;

const searchInput  = document.getElementById('searchInput');
const hazardFilter = document.getElementById('hazardFilter');
const statusFilter = document.getElementById('statusFilter');
const dateFilter    = document.getElementById('dateFilter');
const sortSelect    = document.getElementById('sortSelect');
const tbody          = document.getElementById('reportsBody');
const emptyState     = document.getElementById('emptyState');
const paginationEl   = document.getElementById('pagination');
const deleteModal    = document.getElementById('deleteModal');

[searchInput, hazardFilter, statusFilter, dateFilter, sortSelect].forEach(el => {
  el.addEventListener('input', () => { currentPage = 1; renderTable(); });
});

function statusBadgeClass(status) {
  if (status === "New") return "badge-new";
  if (status === "Investigating") return "badge-investigating";
  if (status === "Resolved") return "badge-resolved";
  return "";
}

function statusLabel(status) {
  return status === "Investigating" ? "Under Investigation" : status;
}

function getFilteredReports() {
  const q = searchInput.value.trim().toLowerCase();
  const hazard = hazardFilter.value;
  const status = statusFilter.value;
  const date = dateFilter.value; // yyyy-mm-dd

  let result = reports.filter(r => {
    const matchesSearch = !q || r.user.toLowerCase().includes(q) || r.hazard.toLowerCase().includes(q);
    const matchesHazard = !hazard || r.hazard === hazard;
    const matchesStatus = !status || r.status === status;
    const matchesDate = !date || r.date.startsWith(date);
    return matchesSearch && matchesHazard && matchesStatus && matchesDate;
  });

  result.sort((a, b) => {
    const diff = new Date(a.date) - new Date(b.date);
    return sortSelect.value === "newest" ? -diff : diff;
  });

  return result;
}

function renderTable() {
  const filtered = getFilteredReports();
  const totalPages = Math.max(1, Math.ceil(filtered.length / ROWS_PER_PAGE));
  if (currentPage > totalPages) currentPage = totalPages;

  const start = (currentPage - 1) * ROWS_PER_PAGE;
  const pageItems = filtered.slice(start, start + ROWS_PER_PAGE);

  tbody.innerHTML = "";

  if (filtered.length === 0) {
    emptyState.style.display = "block";
    document.getElementById('reportsTable').style.display = "none";
  } else {
    emptyState.style.display = "none";
    document.getElementById('reportsTable').style.display = "table";

    pageItems.forEach(r => {
      const tr = document.createElement('tr');
      const dateObj = new Date(r.date);
      const dateStr = dateObj.toLocaleDateString('en-GB', { day: '2-digit', month: 'short' });
      const timeStr = dateObj.toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit' });

      tr.innerHTML = `
        <td>${r.user}</td>
        <td>${dateStr}, ${timeStr}</td>
        <td>${r.hazard}</td>
        <td>${r.lat.toFixed(4)}, ${r.lng.toFixed(4)}</td>
        <td><span class="badge ${statusBadgeClass(r.status)}">${statusLabel(r.status)}</span></td>
        <td>
          <button class="btn-icon view" title="View" onclick="viewReport(${r.id})"><i class="ti ti-eye" aria-hidden="true"></i></button>
          <button class="btn-icon delete" title="Delete" onclick="openDeleteModal(${r.id})"><i class="ti ti-trash" aria-hidden="true"></i></button>
        </td>
      `;
      tbody.appendChild(tr);
    });
  }

  renderPagination(totalPages);
}

function renderPagination(totalPages) {
  paginationEl.innerHTML = "";
  if (totalPages <= 1) return;

  const prevBtn = document.createElement('button');
  prevBtn.textContent = "‹";
  prevBtn.disabled = currentPage === 1;
  prevBtn.onclick = () => { currentPage--; renderTable(); };
  paginationEl.appendChild(prevBtn);

  for (let i = 1; i <= totalPages; i++) {
    const btn = document.createElement('button');
    btn.textContent = i;
    if (i === currentPage) btn.classList.add('active');
    btn.onclick = () => { currentPage = i; renderTable(); };
    paginationEl.appendChild(btn);
  }

  const nextBtn = document.createElement('button');
  nextBtn.textContent = "›";
  nextBtn.disabled = currentPage === totalPages;
  nextBtn.onclick = () => { currentPage++; renderTable(); };
  paginationEl.appendChild(nextBtn);
}

/* ============ VIEW ============
   Replace with your real navigation, e.g.:
   window.location.href = `report-details.html?id=${id}`;
*/
function viewReport(id) {
  window.location.href = `report-details.html?id=${id}`;
}

/* ============ DELETE ============ */
function openDeleteModal(id) {
  pendingDeleteId = id;
  deleteModal.classList.add('show');
}

document.getElementById('cancelDelete').onclick = () => {
  pendingDeleteId = null;
  deleteModal.classList.remove('show');
};

document.getElementById('confirmDelete').onclick = () => {
  /* Replace this with a real API call, e.g.:
     await fetch(`api/delete_report.php?id=${pendingDeleteId}`, { method: 'DELETE' });
     then re-fetch or splice locally on success.
  */
  reports = reports.filter(r => r.id !== pendingDeleteId);
  deleteModal.classList.remove('show');
  renderTable();
};

renderTable();
</script>
</body>
</html>