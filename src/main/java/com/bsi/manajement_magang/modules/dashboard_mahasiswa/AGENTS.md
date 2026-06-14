# Dashboard Mahasiswa API Documentation

This module manages the dashboard statistics and info for the Student (Mahasiswa) dashboard.

## 📂 Code Files
- Controller: [DashboardMahasiswaController.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/dashboard_mahasiswa/controller/DashboardMahasiswaController.java)
- Service: [DashboardMahasiswaService.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/dashboard_mahasiswa/service/DashboardMahasiswaService.java) (impl: [DashboardMahasiswaServiceImpl.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/dashboard_mahasiswa/service/impl/DashboardMahasiswaServiceImpl.java))
- Repository: [DashboardMahasiswaRepository.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/dashboard_mahasiswa/repository/DashboardMahasiswaRepository.java)
- Response DTO: [DashboardMahasiswaStatResponse.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/dashboard_mahasiswa/schema/response/DashboardMahasiswaStatResponse.java)

---

## 🚀 Endpoints Summary

| Method | Endpoint | Auth Required | Description |
|---|---|---|---|
| `GET` | `/api/dashboard-mahasiswa/statistik` | No | Retrieves internship statistics (total attendance and remaining time) for a specific student. |

---

## 📋 Detailed Endpoints & Payloads

### 1. Get Dashboard Statistics
Retrieves statistics for the student dashboard.

- **URL:** `/api/dashboard-mahasiswa/statistik`
- **Method:** `GET`
- **Auth Required:** No (Tanpa Auth)
- **Query Parameters:**
  | Parameter | Type | Required | Description |
  |---|---|---|---|
  | `mahasiswaId` | `UUID` | Yes | The ID of the student (Mahasiswa). |

- **Request Payload:** None (uses Query Parameters)

- **SQL Queries Executed:**
  1. **Count Attendance with 'hadir' status:**
     ```sql
     SELECT COUNT(*) 
     FROM absensi a 
     JOIN periode_magang pm ON a.periode_magang_id = pm.id 
     WHERE pm.mahasiswa_id = :mahasiswaId AND a.status = 'hadir'
     ```
  2. **Find the Latest Internship Period:**
     ```sql
     SELECT tanggal_mulai, tanggal_berakhir, status 
     FROM periode_magang 
     WHERE mahasiswa_id = :mahasiswaId 
     ORDER BY created_at DESC 
     LIMIT 1
     ```

- **Response Payload (`DashboardMahasiswaStatResponse` - HTTP 200 OK):**
  - **Type:** JSON
  - **Structure:**
    ```json
    {
      "totalKehadiran": 12, // long
      "sisaWaktuMagangDays": 45, // long
      "sisaWaktuMagangFormatted": "45 Hari" // string
    }
    ```
  - **Response Formatting Rules (Service Logic):**
    - If no period exists:
      - `sisaWaktuMagangDays`: `0`
      - `sisaWaktuMagangFormatted`: `"Belum terdaftar periode"`
    - If status is `"batal"`:
      - `sisaWaktuMagangDays`: `0`
      - `sisaWaktuMagangFormatted`: `"Magang dibatalkan"`
    - If status is `"selesai"` or the current date is after `tanggal_berakhir`:
      - `sisaWaktuMagangDays`: `0`
      - `sisaWaktuMagangFormatted`: `"Magang selesai"`
    - If the current date is before `tanggal_mulai` (Internship has not started yet):
      - `sisaWaktuMagangDays`: Total days from today to `tanggal_berakhir`
      - `sisaWaktuMagangFormatted`: `"{days} Hari (Belum Mulai)"`
    - Otherwise (active period):
      - `sisaWaktuMagangDays`: Remaining days from today to `tanggal_berakhir`
      - `sisaWaktuMagangFormatted`: `"{days} Hari"`
