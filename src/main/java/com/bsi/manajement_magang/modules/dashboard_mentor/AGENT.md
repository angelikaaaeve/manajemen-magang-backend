# Agent Docs: dashboard_mentor

Detail Endpoint untuk Frontend Developer

## GET `/api/dashboard-mentor/mahasiswa`
**Operation:** searchStudents

### Request Structure
*No Body Request*

### Response Structure
**Type:** `ResponseEntity<APIResponse<List<SearchStudentResponse>>>`
- Refer to ResponseEntity<APIResponse<List<SearchStudentResponse>>>

---

## POST `/api/dashboard-mentor/mahasiswa`
**Operation:** registerStudent

### Request Structure
*No Body Request*

### Response Structure
**Type:** `ResponseEntity<APIResponse<SearchStudentResponse>>`
- `id`: UUID
- `userId`: UUID
- `email`: String
- `nim`: String
- `nama`: String
- `noHp`: String
- `gender`: Gender
- `universitas`: String
- `periodeId`: UUID
- `tanggalMulai`: LocalDate
- `tanggalBerakhir`: LocalDate
- `statusPeriode`: StatusPeriode

---

## GET `/api/dashboard-mentor/statistik`
**Operation:** getDashboardStatistics

### Request Structure
*No Body Request*

### Response Structure
**Type:** `ResponseEntity<APIResponse<DashboardStatResponse>>`
- `jumlahMahasiswaAktif`: long
- `jumlahMahasiswaSelesai`: long
- `rekapAbsensi`: Long>

---

## GET `/api/dashboard-mentor/statistik-kehadiran`
**Operation:** getAttendanceStatsByDateRange

### Request Structure
- `tanggalAwal`: String (Format: yyyy-MM-dd, Query Parameter)
- `tanggalAkhir`: String (Format: yyyy-MM-dd, Query Parameter)

### Response Structure
**Type:** `ResponseEntity<APIResponse<AttendanceStatResponse>>`
- `jumlahHadir`: long
- `jumlahIzin`: long
- `jumlahSakit`: long

---
