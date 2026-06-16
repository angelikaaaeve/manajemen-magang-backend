# Agent Docs: dashboard_mahasiswa

Detail Endpoint untuk Frontend Developer

## GET `/api/dashboard-mahasiswa/statistik`
**Operation:** getDashboardStatistics

### Request Structure
*No Body Request*

### Response Structure
**Type:** `ResponseEntity<APIResponse<DashboardMahasiswaStatResponse>>`
- `totalKehadiran`: long
- `sisaWaktuMagangDays`: long
- `sisaWaktuMagangFormatted`: String

---

