# Kelola Mahasiswa (Student Administration) API Documentation

This module manages student profiles, administrative fields, and academic/industry internship period details.

## 📂 Code Files
- Controller: [DataMahasiswaController.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/data_mahasiswa/controller/DataMahasiswaController.java)
- Service: [DataMahasiswaService.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/data_mahasiswa/service/DataMahasiswaService.java) (impl: [DataMahasiswaServiceImpl.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/data_mahasiswa/service/impl/DataMahasiswaServiceImpl.java))
- Repository: [DataMahasiswaRepository.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/data_mahasiswa/repository/DataMahasiswaRepository.java)
- DTO Schemas:
  - [StudentRequest.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/data_mahasiswa/schema/request/StudentRequest.java)
  - [StudentResponse.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/data_mahasiswa/schema/response/StudentResponse.java)
  - [StudentStatResponse.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/data_mahasiswa/schema/response/StudentStatResponse.java)
  - [UpdateStudentRequest.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/data_mahasiswa/schema/request/UpdateStudentRequest.java)

---

## 🚀 Endpoints Summary

| Method | Endpoint | Auth Required | Description |
|---|---|---|---|
| `POST` | `/api/mahasiswa` | Yes | Registers/adds a new student along with their initial internship period. |
| `PUT` | `/api/mahasiswa/{id}` | Yes | Modifies student identity and/or updates their active internship period. |
| `GET` | `/api/mahasiswa` | Yes | Queries student rosters with filters for gender, university, or status. |
| `GET` | `/api/mahasiswa/statistik` | Yes | Obtains statistical counters of student counts by active status. |
| `GET` | `/api/mahasiswa/{id}` | Yes | Retrieves full profile details for a specific student. |

---

## 📋 Detailed Endpoints & Payloads

### 1. Add Student
Admin adds a new student into the system.

- **URL:** `/api/mahasiswa`
- **Method:** `POST`
- **Headers:** `Authorization: Bearer <token>`, `Content-Type: application/json`
- **Request Payload (`StudentRequest`):**
  ```json
  {
    "email": "budi.santoso@student.ui.ac.id", // Required, unique email format
    "password": "temporarysecurepassword", // Required
    "nim": "2201012001", // Required, unique
    "nama": "Budi Santoso", // Required
    "noHp": "081234567890", // Optional, defaults to "-" if not provided
    "gender": "Laki-laki", // Required, must be "Laki-laki" or "Perempuan"
    "universitas": "Universitas Indonesia", // Required
    "tanggalMulai": "2026-02-01", // Optional (LocalDate yyyy-MM-dd)
    "tanggalBerakhir": "2026-07-31", // Optional (LocalDate yyyy-MM-dd)
    "periodeStatus": "aktif" // Optional, defaults to "aktif"
  }
  ```
- **SQL Queries Executed:**
  1. Uniqueness checks:
     ```sql
     SELECT COUNT(1) FROM "user" WHERE email = :email;
     SELECT COUNT(1) FROM mahasiswa WHERE nim = :nim;
     ```
  2. Saves user record:
     ```sql
     INSERT INTO "user" (id, email, password, role, is_active, created_at, updated_at) 
     VALUES (:id, :email, :password, 'mahasiswa', true, NOW(), NOW())
     ```
  3. Saves student profile:
     ```sql
     INSERT INTO mahasiswa (id, user_id, nim, nama, no_hp, gender, universitas) 
     VALUES (:id, :userId, :nim, :nama, :noHp, :gender, :universitas)
     ```
  4. Saves internship period (if start & end date are specified):
     ```sql
     INSERT INTO periode_magang (id, mahasiswa_id, tanggal_mulai, tanggal_berakhir, status, created_at) 
     VALUES (:id, :mahasiswaId, :tanggalMulai, :tanggalBerakhir, :status, NOW())
     ```
- **Response Payload (`StudentResponse` - HTTP 201 Created):**
  ```json
  {
    "id": "f81d4fae-7dec-11d0-a765-00a0c91e6bf6", // Student ID (UUID)
    "userId": "9e248b11-236b-4ac4-8bde-d23a6f1ea124", // IAM User ID (UUID)
    "email": "budi.santoso@student.ui.ac.id",
    "nim": "2201012001",
    "nama": "Budi Santoso",
    "noHp": "081234567890",
    "gender": "Laki-laki",
    "universitas": "Universitas Indonesia",
    "periodeId": "5c1a8d9b-2e9c-4aa4-8f7b-23fcd10d9e81", // UUID, can be null
    "tanggalMulai": "2026-02-01", // yyyy-MM-dd, can be null
    "tanggalBerakhir": "2026-07-31", // yyyy-MM-dd, can be null
    "statusPeriode": "aktif", // String, can be null
    "mentorId": "3cb1ab0d-4ea3-4cfb-81d0-d3cdb2413e11", // UUID, can be null
    "namaMentor": "Dr. Ahmad Hidayat, M.T." // String, can be null
  }
  ```

---

### 2. Update Student Profile & Period
Admin modifies the profile or active internship period details of an existing student.

- **URL:** `/api/mahasiswa/{id}`
- **Method:** `PUT`
- **Headers:** `Authorization: Bearer <token>`, `Content-Type: application/json`
- **Path Parameter:**
  - `id` (UUID): Unique student ID
- **Request Payload (`UpdateStudentRequest`):**
  ```json
  {
    "email": "budi.santoso@student.ui.ac.id",
    "nim": "2201012001",
    "nama": "Budi Santoso",
    "noHp": "081234567890",
    "gender": "Laki-laki",
    "universitas": "Universitas Indonesia",
    "periode": {
      "tanggalMulai": "2026-02-01",
      "tanggalBerakhir": "2026-08-31", // Extended period
      "status": "aktif" // e.g. "aktif" | "selesai" | "batal"
    }
  }
  ```
- **SQL Queries Executed:**
  1. Checks if student exists:
     ```sql
     SELECT m.id, m.user_id, u.email, m.nim, m.nama, m.no_hp, m.gender, m.universitas, ...
     FROM mahasiswa m JOIN "user" u ON m.user_id = u.id WHERE m.id = :id
     ```
  2. Checks email uniqueness (if changed):
     ```sql
     SELECT COUNT(1) FROM "user" WHERE email = :email AND id <> :userId;
     -- If valid, updates email:
     UPDATE "user" SET email = :email, updated_at = NOW() WHERE id = :userId;
     ```
  3. Updates student profile:
     ```sql
     UPDATE mahasiswa SET nim = :nim, nama = :nama, no_hp = :noHp, gender = :gender, universitas = :universitas WHERE id = :id
     ```
  4. Updates or creates internship period (if `periode` block is specified):
     - Check latest period:
       ```sql
       SELECT id, tanggal_mulai, tanggal_berakhir, status FROM periode_magang WHERE mahasiswa_id = :studentId ORDER BY created_at DESC LIMIT 1
       ```
     - If exists, update:
       ```sql
       UPDATE periode_magang SET tanggal_mulai = :tanggalMulai, tanggal_berakhir = :tanggalBerakhir, status = :status WHERE id = :id
       ```
     - If not exists, insert:
       ```sql
       INSERT INTO periode_magang (id, mahasiswa_id, tanggal_mulai, tanggal_berakhir, status, created_at) VALUES (:id, :mahasiswaId, :tanggalMulai, :tanggalBerakhir, :status, NOW())
       ```
- **Response Payload (`StudentResponse` - HTTP 200 OK):**
  ```json
  {
    "id": "f81d4fae-7dec-11d0-a765-00a0c91e6bf6",
    "userId": "9e248b11-236b-4ac4-8bde-d23a6f1ea124",
    "email": "budi.santoso@student.ui.ac.id",
    "nim": "2201012001",
    "nama": "Budi Santoso",
    "noHp": "081234567890",
    "gender": "Laki-laki",
    "universitas": "Universitas Indonesia",
    "periodeId": "5c1a8d9b-2e9c-4aa4-8f7b-23fcd10d9e81",
    "tanggalMulai": "2026-02-01",
    "tanggalBerakhir": "2026-08-31",
    "statusPeriode": "aktif",
    "mentorId": "3cb1ab0d-4ea3-4cfb-81d0-d3cdb2413e11",
    "namaMentor": "Dr. Ahmad Hidayat, M.T."
  }
  ```

---

### 3. List Students
Retrieve list of registered students in the system.

- **URL:** `/api/mahasiswa`
- **Method:** `GET`
- **Headers:** `Authorization: Bearer <token>`
- **Query Parameters:**
  - `gender` (String, optional): Filter by `Laki-laki` | `Perempuan`
  - `universitas` (String, optional): Filter by university name
  - `status` (String, optional): Filter by period status (`aktif` | `selesai` | `Belum Penempatan` (case-insensitive))
- **SQL Query Executed:**
  ```sql
  SELECT m.id, m.user_id, u.email, m.nim, m.nama, m.no_hp, m.gender, m.universitas, 
         pm.id as periode_id, pm.tanggal_mulai, pm.tanggal_berakhir, pm.status as status_periode, 
         men.id as mentor_id, men.nama as nama_mentor 
  FROM mahasiswa m 
  JOIN "user" u ON m.user_id = u.id 
  LEFT JOIN ( 
      SELECT DISTINCT ON (mahasiswa_id) id, mahasiswa_id, tanggal_mulai, tanggal_berakhir, status 
      FROM periode_magang 
      ORDER BY mahasiswa_id, created_at DESC 
  ) pm ON m.id = pm.mahasiswa_id 
  LEFT JOIN mentor_mahasiswa mm ON m.id = mm.mahasiswa_id 
  LEFT JOIN mentor men ON mm.mentor_id = men.id 
  WHERE 1=1 
  -- IF gender parameter is provided
  AND m.gender = :gender 
  -- IF universitas parameter is provided
  AND m.universitas = :universitas 
  -- IF status is 'Belum Penempatan'
  AND pm.status IS NULL 
  -- IF status is anything else (e.g. 'aktif', 'selesai')
  AND pm.status = :status 
  ORDER BY m.nama ASC
  ```
- **Response Payload (`List<StudentResponse>` - HTTP 200 OK):**
  ```json
  [
    {
      "id": "f81d4fae-7dec-11d0-a765-00a0c91e6bf6",
      "userId": "9e248b11-236b-4ac4-8bde-d23a6f1ea124",
      "email": "budi.santoso@student.ui.ac.id",
      "nim": "2201012001",
      "nama": "Budi Santoso",
      "noHp": "081234567890",
      "gender": "Laki-laki",
      "universitas": "Universitas Indonesia",
      "periodeId": "5c1a8d9b-2e9c-4aa4-8f7b-23fcd10d9e81",
      "tanggalMulai": "2026-02-01",
      "tanggalBerakhir": "2026-07-31",
      "statusPeriode": "aktif",
      "mentorId": "3cb1ab0d-4ea3-4cfb-81d0-d3cdb2413e11",
      "namaMentor": "Dr. Ahmad Hidayat, M.T."
    }
  ]
  ```

---

### 4. Student Roster Statistics
Retrieve cumulative counts of active, completed, and active-unassessed students.

- **URL:** `/api/mahasiswa/statistik`
- **Method:** `GET`
- **Headers:** `Authorization: Bearer <token>`
- **Query Parameters (Optional):**
  - `gender` (String): Filter statistics by gender
  - `universitas` (String): Filter statistics by university name
- **SQL Queries Executed:**
  ```sql
  -- 1. Count Active
  SELECT COUNT(DISTINCT m.id) FROM mahasiswa m 
  JOIN (SELECT DISTINCT ON (mahasiswa_id) mahasiswa_id, status FROM periode_magang ORDER BY mahasiswa_id, created_at DESC) pm 
  ON m.id = pm.mahasiswa_id 
  WHERE pm.status = 'aktif' [AND m.gender = :gender] [AND m.universitas = :universitas];

  -- 2. Count Completed
  SELECT COUNT(DISTINCT m.id) FROM mahasiswa m 
  JOIN (SELECT DISTINCT ON (mahasiswa_id) mahasiswa_id, status FROM periode_magang ORDER BY mahasiswa_id, created_at DESC) pm 
  ON m.id = pm.mahasiswa_id 
  WHERE pm.status = 'selesai' [AND m.gender = :gender] [AND m.universitas = :universitas];

  -- 3. Count Active & Unassessed
  SELECT COUNT(DISTINCT m.id) FROM mahasiswa m 
  JOIN periode_magang pm ON m.id = pm.mahasiswa_id 
  LEFT JOIN penilaian p ON pm.id = p.periode_magang_id 
  WHERE pm.status = 'aktif' AND p.id IS NULL [AND m.gender = :gender] [AND m.universitas = :universitas];
  ```
- **Response Payload (`StudentStatResponse` - HTTP 200 OK):**
  ```json
  {
    "totalAktif": 145,
    "totalSelesai": 32,
    "totalAktifTanpaPenilaian": 18
  }
  ```

---

### 5. Student Detail
Gets a student profile by their unique student ID.

- **URL:** `/api/mahasiswa/{id}`
- **Method:** `GET`
- **Headers:** `Authorization: Bearer <token>`
- **Path Parameter:**
  - `id` (UUID): Unique student ID
- **Response Payload (`StudentResponse` - HTTP 200 OK):**
  ```json
  {
    "id": "f81d4fae-7dec-11d0-a765-00a0c91e6bf6",
    "userId": "9e248b11-236b-4ac4-8bde-d23a6f1ea124",
    "email": "budi.santoso@student.ui.ac.id",
    "nim": "2201012001",
    "nama": "Budi Santoso",
    "noHp": "081234567890",
    "gender": "Laki-laki",
    "universitas": "Universitas Indonesia",
    "periodeId": "5c1a8d9b-2e9c-4aa4-8f7b-23fcd10d9e81",
    "tanggalMulai": "2026-02-01",
    "tanggalBerakhir": "2026-07-31",
    "statusPeriode": "aktif",
    "mentorId": "3cb1ab0d-4ea3-4cfb-81d0-d3cdb2413e11",
    "namaMentor": "Dr. Ahmad Hidayat, M.T."
  }
  ```
