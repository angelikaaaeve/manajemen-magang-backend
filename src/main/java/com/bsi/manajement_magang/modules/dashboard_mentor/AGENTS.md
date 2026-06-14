# Dashboard Mentor API Documentation

This module manages the student query/search, registration (daftarkan mahasiswa baru), and dashboard statistics (active/completed students count & attendance accumulation charts) for the Mentor dashboard.

## 📂 Code Files
- Controller: [DashboardMentorController.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/dashboard_mentor/controller/DashboardMentorController.java)
- Service: [DashboardMentorService.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/dashboard_mentor/service/DashboardMentorService.java) (impl: [DashboardMentorServiceImpl.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/dashboard_mentor/service/impl/DashboardMentorServiceImpl.java))
- Repository: [DashboardMentorRepository.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/dashboard_mentor/repository/DashboardMentorRepository.java)
- DTO Schemas:
  - [DashboardStatResponse.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/dashboard_mentor/schema/response/DashboardStatResponse.java)
  - [RegisterStudentRequest.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/dashboard_mentor/schema/request/RegisterStudentRequest.java)
  - [SearchStudentResponse.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/dashboard_mentor/schema/response/SearchStudentResponse.java)

---

## 🚀 Endpoints Summary

| Method | Endpoint | Auth Required | Description |
|---|---|---|---|
| `GET` | `/api/dashboard-mentor/mahasiswa` | Yes | Queries/searches student details by name. |
| `POST` | `/api/dashboard-mentor/mahasiswa` | No | Registers/adds a new student account (Mahasiswa) along with an optional internship period. |
| `GET` | `/api/dashboard-mentor/statistik` | No | Retrieves active/completed student count metrics and an attendance accumulation breakdown. |

---

## 📋 Detailed Endpoints & Payloads

### 1. Search Students
Allows mentors to search for students in the system by name.

- **URL:** `/api/dashboard-mentor/mahasiswa`
- **Method:** `GET`
- **Query Parameters:**
  | Parameter | Type | Required | Description |
  |---|---|---|---|
  | `nama` | `String` | No | Filter string to query students by name (case-insensitive substring match). |

- **Request Payload:** None

- **SQL Query Executed:**
  ```sql
  SELECT m.id, m.user_id, u.email, m.nim, m.nama, m.no_hp, m.gender, m.universitas,
         pm.id as periode_id, pm.tanggal_mulai, pm.tanggal_berakhir, pm.status as status_periode
  FROM mahasiswa m
  JOIN "user" u ON m.user_id = u.id
  LEFT JOIN (
      SELECT DISTINCT ON (mahasiswa_id) id, mahasiswa_id, tanggal_mulai, tanggal_berakhir, status
      FROM periode_magang
      ORDER BY mahasiswa_id, created_at DESC
  ) pm ON m.id = pm.mahasiswa_id
  WHERE 1=1 AND LOWER(m.nama) LIKE LOWER(:name) -- If name parameter is provided
  ORDER BY m.nama ASC
  ```

- **Response Payload (`List<SearchStudentResponse>` - HTTP 200 OK):**
  - **Type:** JSON Array
  - **Structure:**
    ```json
    [
      {
        "id": "e0bfa511-df4e-4b68-b789-9a07a123bc45", // UUID
        "userId": "9bf2027e-8c3b-4899-b1ff-92a1abcdf012", // UUID
        "email": "student@universitas.ac.id",
        "nim": "12345678",
        "nama": "Farhan Aditama",
        "noHp": "081234567890",
        "gender": "Laki-laki", // "Laki-laki" | "Perempuan"
        "universitas": "Universitas Indonesia",
        "periodeId": "5f60b73c-74a9-4672-9bd7-5264b281abcd", // UUID, can be null
        "tanggalMulai": "2026-06-01", // LocalDate, can be null
        "tanggalBerakhir": "2026-09-01", // LocalDate, can be null
        "statusPeriode": "aktif" // String (e.g. "aktif", "selesai", "batal"), can be null
      }
    ]
    ```

---

### 2. Register New Student (Daftarkan Mahasiswa Baru)
Registers a new student account, including user credentials, student profile, and their active internship period.

- **URL:** `/api/dashboard-mentor/mahasiswa`
- **Method:** `POST`
- **Auth Required:** No (Tanpa Auth)
- **Request Payload (`RegisterStudentRequest`):**
  - **Type:** JSON
  - **Structure:**
    ```json
    {
      "email": "farhan@student.ui.ac.id", // Valid email format, required
      "password": "securepassword", // Required
      "nim": "12345678", // Required
      "nama": "Farhan Aditama", // Required
      "noHp": "081234567890", // Optional, defaults to "-" if not provided
      "gender": "Laki-laki", // Required, must be "Laki-laki" or "Perempuan"
      "universitas": "Universitas Indonesia", // Required
      "tanggalMulai": "2026-06-01", // Optional (LocalDate yyyy-MM-dd)
      "tanggalBerakhir": "2026-09-01" // Optional (LocalDate yyyy-MM-dd)
    }
    ```

- **SQL Queries Executed:**
  1. Checks if email or NIM exists:
     ```sql
     SELECT COUNT(1) FROM "user" WHERE email = :email;
     SELECT COUNT(1) FROM mahasiswa WHERE nim = :nim;
     ```
  2. Saves user authentication entry:
     ```sql
     INSERT INTO "user" (id, email, password, role, is_active, created_at, updated_at)
     VALUES (:id, :email, :password, 'mahasiswa', true, NOW(), NOW())
     ```
  3. Saves student profile:
     ```sql
     INSERT INTO mahasiswa (id, user_id, nim, nama, no_hp, gender, universitas)
     VALUES (:id, :userId, :nim, :nama, :noHp, :gender, :universitas)
     ```
  4. Saves internship period (if `tanggalMulai` and `tanggalBerakhir` are provided):
     ```sql
     INSERT INTO periode_magang (id, mahasiswa_id, tanggal_mulai, tanggal_berakhir, status, created_at)
     VALUES (:id, :mahasiswaId, :tanggalMulai, :tanggalBerakhir, 'aktif', NOW())
     ```

- **Response Payload (`SearchStudentResponse` - HTTP 201 Created):**
  - **Type:** JSON
  - **Structure:**
    ```json
    {
      "id": "e0bfa511-df4e-4b68-b789-9a07a123bc45",
      "userId": "9bf2027e-8c3b-4899-b1ff-92a1abcdf012",
      "email": "farhan@student.ui.ac.id",
      "nim": "12345678",
      "nama": "Farhan Aditama",
      "noHp": "081234567890",
      "gender": "Laki-laki",
      "universitas": "Universitas Indonesia",
      "periodeId": "5f60b73c-74a9-4672-9bd7-5264b281abcd",
      "tanggalMulai": "2026-06-01",
      "tanggalBerakhir": "2026-09-01",
      "statusPeriode": "aktif"
    }
    ```

---

### 3. Get Dashboard Statistics
Retrieves statistical count of active and completed students, and maps accumulated attendance categories (`hadir`, `izin`, `sakit`) for chart/diagram presentation.

- **URL:** `/api/dashboard-mentor/statistik`
- **Method:** `GET`
- **Auth Required:** No (Tanpa Auth)
- **Query Parameters:**
  | Parameter | Type | Required | Description |
  |---|---|---|---|
  | `mentorId` | `UUID` | No | Mentor's ID. If provided, stats are filtered for students assigned to that mentor. If omitted, global stats are returned. |

- **Request Payload:** None

- **SQL Queries Executed:**
  1. **Count Active Students:**
     - *Filtered by Mentor:*
       ```sql
       SELECT COUNT(DISTINCT mm.mahasiswa_id)
       FROM mentor_mahasiswa mm
       JOIN periode_magang pm ON mm.mahasiswa_id = pm.mahasiswa_id
       WHERE mm.mentor_id = :mentorId AND pm.status = 'aktif'
       ```
     - *Global (if mentorId is absent):*
       ```sql
       SELECT COUNT(DISTINCT mahasiswa_id) FROM periode_magang WHERE status = 'aktif'
       ```
  2. **Count Completed Students:**
     - *Filtered by Mentor:*
       ```sql
       SELECT COUNT(DISTINCT mm.mahasiswa_id)
       FROM mentor_mahasiswa mm
       JOIN periode_magang pm ON mm.mahasiswa_id = pm.mahasiswa_id
       WHERE mm.mentor_id = :mentorId AND pm.status = 'selesai'
       ```
     - *Global (if mentorId is absent):*
       ```sql
       SELECT COUNT(DISTINCT mahasiswa_id) FROM periode_magang WHERE status = 'selesai'
       ```
  3. **Accumulate Attendance Breakdown:**
     - *Filtered by Mentor:*
       ```sql
       SELECT a.status, COUNT(*) as status_count
       FROM absensi a
       JOIN periode_magang pm ON a.periode_magang_id = pm.id
       JOIN mentor_mahasiswa mm ON pm.mahasiswa_id = mm.mahasiswa_id
       WHERE mm.mentor_id = :mentorId AND a.status IN ('hadir', 'izin', 'sakit')
       GROUP BY a.status
       ```
     - *Global (if mentorId is absent):*
       ```sql
       SELECT status, COUNT(*) as status_count
       FROM absensi
       WHERE status IN ('hadir', 'izin', 'sakit')
       GROUP BY status
       ```

- **Response Payload (`DashboardStatResponse` - HTTP 200 OK):**
  - **Type:** JSON
  - **Structure:**
    ```json
    {
      "jumlahMahasiswaAktif": 15,
      "jumlahMahasiswaSelesai": 8,
      "rekapAbsensi": {
        "hadir": 240,
        "izin": 12,
        "sakit": 5
      }
    }
    ```
