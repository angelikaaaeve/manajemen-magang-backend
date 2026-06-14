# Penilaian (Evaluation & Grading) API Documentation

This module manages the internship evaluation process, enabling mentors to submit scores across multiple performance criteria (kinerja, kedisiplinan, responsibility, etc.) and view total score averages.

## 📂 Code Files
- Controller: [PenilaianController.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/penilaian/controller/PenilaianController.java)
- Service: [PenilaianService.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/penilaian/service/PenilaianService.java) (impl: [PenilaianServiceImpl.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/penilaian/service/impl/PenilaianServiceImpl.java))
- Repository: [PenilaianRepository.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/penilaian/repository/PenilaianRepository.java)
- DTO Schemas:
  - [PenilaianRequest.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/penilaian/schema/request/PenilaianRequest.java)
  - [PenilaianResponse.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/penilaian/schema/response/PenilaianResponse.java)
  - [PenilaianStatResponse.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/penilaian/schema/response/PenilaianStatResponse.java)

---

## 🚀 Endpoints Summary

| Method | Endpoint | Auth Required | Description |
|---|---|---|---|
| `GET` | `/api/penilaian` | Yes | Retrieves list of student evaluations/grades with optional filters. |
| `POST` | `/api/penilaian` | Yes | Creates or updates an evaluation sheet for a student's internship period. |
| `GET` | `/api/penilaian/statistik` | Yes | Obtains statistical counters of student evaluations (graded vs ungraded). |

---

## 📋 Detailed Endpoints & Payloads

### 1. List Student Evaluations
Get internship evaluation logs (Mentor).

- **URL:** `/api/penilaian`
- **Method:** `GET`
- **Headers:** `Authorization: Bearer <token>`
- **Query Parameters:**
  - `status` (String, optional): Filter by appraisal status (options: `sudah dinilai` | `belum dinilai` | `semua status`).
  - `namaMahasiswa` (String, optional): Filter by student name substring (ILike query).
- **SQL Query Executed:**
  ```sql
  SELECT pm.id as periode_id, pm.mahasiswa_id, m.nim, m.nama as nama_mahasiswa, 
         p.id as penilaian_id, p.mentor_id, men.nama as nama_mentor, 
         p.kinerja, p.kedisiplinan, p.tanggung_jawab, p.komunikasi, 
         p.sikap, p.kerapihan, p.absensi, p.kerjasama, p.nilai_total, p.catatan 
  FROM periode_magang pm 
  JOIN mahasiswa m ON pm.mahasiswa_id = m.id 
  LEFT JOIN penilaian p ON pm.id = p.periode_magang_id 
  LEFT JOIN mentor men ON p.mentor_id = men.id 
  WHERE pm.status = 'aktif' 
  -- IF status is 'sudah dinilai'
  AND p.id IS NOT NULL 
  -- IF status is 'belum dinilai'
  AND p.id IS NULL 
  -- IF namaMahasiswa parameter is provided
  AND m.nama ILIKE :namaMahasiswa 
  ORDER BY m.nama ASC
  ```
- **Response Payload (`List<PenilaianResponse>` - HTTP 200 OK):**
  - **Type:** JSON Array
  - **Structure:**
    ```json
    [
      {
        "id": "a90f7d8c-4a3b-410d-8f2c-5b3cf1e9a2b8", // UUID, can be null
        "periodeMagangId": "5c1a8d9b-2e9c-4aa4-8f7b-23fcd10d9e81", // UUID
        "mahasiswaId": "f81d4fae-7dec-11d0-a765-00a0c91e6bf6", // UUID
        "nim": "2201012001",
        "namaMahasiswa": "Budi Santoso",
        "mentorId": "3cb1ab0d-4ea3-4cfb-81d0-d3cdb2413e11", // UUID, can be null
        "namaMentor": "Dr. Ahmad Hidayat, M.T.", // String, can be null
        "kinerja": 88.0, // BigDecimal, can be null
        "kedisiplinan": 90.0, // BigDecimal, can be null
        "tanggungJawab": 85.0, // BigDecimal, can be null
        "komunikasi": 85.0, // BigDecimal, can be null
        "sikap": 92.0, // BigDecimal, can be null
        "kerapihan": 80.0, // BigDecimal, can be null
        "absensi": 96.0, // BigDecimal, can be null
        "kerjasama": 88.0, // BigDecimal, can be null
        "nilaiTotal": 88.0, // BigDecimal, average score, can be null
        "catatan": "Performa pengerjaan fitur integration test Next.js sangat baik.", // String, defaults to "-"
        "statusPenilaian": "SUDAH_DINILAI" // "SUDAH_DINILAI" | "BELUM_DINILAI"
      }
    ]
    ```

---

### 2. Create or Update Student Evaluation
Mentor submits or modifies scores for a student.

- **URL:** `/api/penilaian`
- **Method:** `POST`
- **Headers:** `Authorization: Bearer <token>`, `Content-Type: application/json`
- **Request Payload (`PenilaianRequest`):**
  - **Type:** JSON
  - **Structure:**
    ```json
    {
      "periodeMagangId": "5c1a8d9b-2e9c-4aa4-8f7b-23fcd10d9e81", // Required (UUID)
      "mentorId": "3cb1ab0d-4ea3-4cfb-81d0-d3cdb2413e11", // Required (UUID)
      "kinerja": 88.0, // BigDecimal, required, range [0-100]
      "kedisiplinan": 90.0, // BigDecimal, required, range [0-100]
      "tanggungJawab": 85.0, // BigDecimal, required, range [0-100]
      "komunikasi": 85.0, // BigDecimal, required, range [0-100]
      "sikap": 92.0, // BigDecimal, required, range [0-100]
      "kerapihan": 80.0, // BigDecimal, required, range [0-100]
      "absensi": 96.0, // BigDecimal, required, range [0-100]
      "kerjasama": 88.0, // BigDecimal, required, range [0-100]
      "catatan": "Performa pengerjaan fitur integration test Next.js sangat baik." // String
    }
    ```
- **SQL Queries Executed:**
  1. Validates references in database:
     ```sql
     SELECT COUNT(1) FROM periode_magang WHERE id = :periodId;
     SELECT COUNT(1) FROM mentor WHERE id = :mentorId;
     ```
  2. Checks if assessment already exists for the period:
     ```sql
     SELECT pm.id as periode_id, ... FROM periode_magang pm LEFT JOIN penilaian p ON pm.id = p.periode_magang_id ... WHERE pm.id = :periodId
     ```
  3. If not exists, saves new entry:
     ```sql
     INSERT INTO penilaian (id, periode_magang_id, mentor_id, kinerja, kedisiplinan, tanggung_jawab, komunikasi, sikap, kerapihan, absensi, kerjasama, catatan, created_at) 
     VALUES (:id, :periodeMagangId, :mentorId, :kinerja, :kedisiplinan, :tanggungJawab, :komunikasi, :sikap, :kerapihan, :absensi, :kerjasama, :catatan, NOW())
     ```
  4. If exists, updates entry:
     ```sql
     UPDATE penilaian 
     SET mentor_id = :mentorId, kinerja = :kinerja, kedisiplinan = :kedisiplinan, tanggung_jawab = :tanggungJawab, komunikasi = :komunikasi, sikap = :sikap, kerapihan = :kerapihan, absensi = :absensi, kerjasama = :kerjasama, catatan = :catatan 
     WHERE id = :id
     ```
- **Response Payload (`PenilaianResponse` - HTTP 200 OK):**
  ```json
  {
    "id": "a90f7d8c-4a3b-410d-8f2c-5b3cf1e9a2b8",
    "periodeMagangId": "5c1a8d9b-2e9c-4aa4-8f7b-23fcd10d9e81",
    "mahasiswaId": "f81d4fae-7dec-11d0-a765-00a0c91e6bf6",
    "nim": "2201012001",
    "namaMahasiswa": "Budi Santoso",
    "mentorId": "3cb1ab0d-4ea3-4cfb-81d0-d3cdb2413e11",
    "namaMentor": "Dr. Ahmad Hidayat, M.T.",
    "kinerja": 88.0,
    "kedisiplinan": 90.0,
    "tanggungJawab": 85.0,
    "komunikasi": 85.0,
    "sikap": 92.0,
    "kerapihan": 80.0,
    "absensi": 96.0,
    "kerjasama": 88.0,
    "nilaiTotal": 88.0, // Automatically recalculated
    "catatan": "Performa pengerjaan fitur integration test Next.js sangat baik.",
    "statusPenilaian": "SUDAH_DINILAI"
  }
  ```

---

### 3. Evaluation Status Statistics
Retrieve general evaluation metrics counts.

- **URL:** `/api/penilaian/statistik`
- **Method:** `GET`
- **Headers:** `Authorization: Bearer <token>`
- **Query Parameter (Optional):**
  - `namaMahasiswa` (String): Filter statistics by student name (ILike query).
- **SQL Queries Executed:**
  ```sql
  -- 1. Count Total active students
  SELECT COUNT(1) FROM periode_magang pm JOIN mahasiswa m ON pm.mahasiswa_id = m.id WHERE pm.status = 'aktif' [AND m.nama ILIKE :namaMahasiswa];

  -- 2. Count Graded
  SELECT COUNT(1) FROM periode_magang pm JOIN mahasiswa m ON pm.mahasiswa_id = m.id JOIN penilaian p ON pm.id = p.periode_magang_id WHERE pm.status = 'aktif' [AND m.nama ILIKE :namaMahasiswa];

  -- 3. Count Ungraded
  SELECT COUNT(1) FROM periode_magang pm JOIN mahasiswa m ON pm.mahasiswa_id = m.id LEFT JOIN penilaian p ON pm.id = p.periode_magang_id WHERE pm.status = 'aktif' AND p.id IS NULL [AND m.nama ILIKE :namaMahasiswa];
  ```
- **Response Payload (`PenilaianStatResponse` - HTTP 200 OK):**
  ```json
  {
    "totalPenilaian": 177,
    "totalSudahDinilai": 159,
    "totalBelumDinilai": 18
  }
  ```
