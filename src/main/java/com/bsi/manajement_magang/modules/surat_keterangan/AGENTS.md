# Surat Keterangan (Completion Letters) API Documentation

This module manages official completion letters (surat keterangan selesai magang) issued to students after their internship periods conclude.

## 📂 Code Files
- Controller: [SuratKeteranganController.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/surat_keterangan/controller/SuratKeteranganController.java)
- Service: [SuratKeteranganService.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/surat_keterangan/service/SuratKeteranganService.java) (impl: [SuratKeteranganServiceImpl.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/surat_keterangan/service/impl/SuratKeteranganServiceImpl.java))
- Repository: [SuratKeteranganRepository.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/surat_keterangan/repository/SuratKeteranganRepository.java)
- DTO Schemas:
  - [SuratKeteranganRequest.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/surat_keterangan/schema/request/SuratKeteranganRequest.java)
  - [SuratKeteranganResponse.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/surat_keterangan/schema/response/SuratKeteranganResponse.java)
  - [SuratKeteranganStatResponse.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/surat_keterangan/schema/response/SuratKeteranganStatResponse.java)

---

## 🚀 Endpoints Summary

| Method | Endpoint | Auth Required | Description |
|---|---|---|---|
| `GET` | `/api/surat-keterangan` | Yes | Retrieves list of student completion letters with optional filters. |
| `POST` | `/api/surat-keterangan` | Yes | Uploads/registers an official completion letter for a student's internship period. |
| `GET` | `/api/surat-keterangan/statistik` | Yes | Obtains statistical counters of completion letter statuses. |

---

## 📋 Detailed Endpoints & Payloads

### 1. List Completion Letters
Get uploaded completion letters with optional filters (Mentor).

- **URL:** `/api/surat-keterangan`
- **Method:** `GET`
- **Headers:** `Authorization: Bearer <token>`
- **Query Parameters:**
  - `status` (String, optional): Filter by letter status (options: `sudah diunggah` | `belum diunggah` | `semua status` - case insensitive)
  - `namaMahasiswa` (String, optional): Filter by student name substring (ILike query)
- **SQL Query Executed:**
  ```sql
  SELECT pm.id as periode_id, pm.mahasiswa_id, m.nim, m.nama as nama_mahasiswa, 
         sk.id as surat_id, sk.url, sk.created_at 
  FROM periode_magang pm 
  JOIN mahasiswa m ON pm.mahasiswa_id = m.id 
  LEFT JOIN surat_keterangan_magang sk ON pm.id = sk.periode_magang_id 
  WHERE pm.status = 'aktif' 
  -- IF status is 'sudah diunggah'
  AND sk.id IS NOT NULL 
  -- IF status is 'belum diunggah'
  AND sk.id IS NULL 
  -- IF namaMahasiswa parameter is provided
  AND m.nama ILIKE :namaMahasiswa 
  ORDER BY m.nama ASC
  ```
- **Response Payload (`List<SuratKeteranganResponse>` - HTTP 200 OK):**
  - **Type:** JSON Array
  - **Structure:**
    ```json
    [
      {
        "id": "e81d4fae-7dec-11d0-a765-00a0c91e6bf6", // UUID, can be null
        "periodeMagangId": "5c1a8d9b-2e9c-4aa4-8f7b-23fcd10d9e81", // UUID
        "mahasiswaId": "f81d4fae-7dec-11d0-a765-00a0c91e6bf6", // UUID
        "nim": "2201012001",
        "namaMahasiswa": "Budi Santoso",
        "url": "/uploads/letters/budi_completion_letter.pdf", // String, defaults to "-" if none
        "statusSurat": "Sudah Diunggah", // "Sudah Diunggah" | "belum diunggah"
        "createdAt": "2026-05-31T14:05:00" // LocalDateTime (ISO-8601 without offset), can be null
      }
    ]
    ```

---

### 2. Upload/Register Completion Letter
Mentors or admins register a completion letter file.

- **URL:** `/api/surat-keterangan`
- **Method:** `POST`
- **Headers:** `Authorization: Bearer <token>`, `Content-Type: application/json`
- **Request Payload (`SuratKeteranganRequest`):**
  - **Type:** JSON
  - **Structure:**
    ```json
    {
      "periodeMagangId": "5c1a8d9b-2e9c-4aa4-8f7b-23fcd10d9e81", // Required (UUID)
      "url": "/uploads/letters/budi_completion_letter.pdf" // Required (String)
    }
    ```
- **SQL Queries Executed:**
  1. Validates period exists:
     ```sql
     SELECT COUNT(1) FROM periode_magang WHERE id = :periodId
     ```
  2. Checks if completion letter entry already exists for the period:
     ```sql
     SELECT pm.id as periode_id, ... FROM periode_magang pm LEFT JOIN surat_keterangan_magang sk ON pm.id = sk.periode_magang_id ... WHERE pm.id = :periodId
     ```
  3. If exists, update URL:
     ```sql
     UPDATE surat_keterangan_magang SET url = :url WHERE id = :id
     ```
  4. If not exists, insert new:
     ```sql
     INSERT INTO surat_keterangan_magang (id, periode_magang_id, url, created_at) VALUES (:id, :periodeMagangId, :url, NOW())
     ```
- **Response Payload (`SuratKeteranganResponse` - HTTP 200 OK):**
  ```json
  {
    "id": "e81d4fae-7dec-11d0-a765-00a0c91e6bf6",
    "periodeMagangId": "5c1a8d9b-2e9c-4aa4-8f7b-23fcd10d9e81",
    "mahasiswaId": "f81d4fae-7dec-11d0-a765-00a0c91e6bf6",
    "nim": "2201012001",
    "namaMahasiswa": "Budi Santoso",
    "url": "/uploads/letters/budi_completion_letter.pdf",
    "statusSurat": "Sudah Diunggah",
    "createdAt": "2026-05-31T14:05:00"
  }
  ```

---

### 3. Completion Letter Statistics
Retrieve cumulative completion letter statistics (Mentor).

- **URL:** `/api/surat-keterangan/statistik`
- **Method:** `GET`
- **Headers:** `Authorization: Bearer <token>`
- **Query Parameter (Optional):**
  - `namaMahasiswa` (String): Filter statistics by student name (ILike query)
- **SQL Queries Executed:**
  ```sql
  -- 1. Total Jumlah Surat (Active Periods)
  SELECT COUNT(1) FROM periode_magang pm JOIN mahasiswa m ON pm.mahasiswa_id = m.id WHERE pm.status = 'aktif' [AND m.nama ILIKE :namaMahasiswa];

  -- 2. Total Surat Diunggah
  SELECT COUNT(1) FROM periode_magang pm JOIN mahasiswa m ON pm.mahasiswa_id = m.id JOIN surat_keterangan_magang sk ON pm.id = sk.periode_magang_id WHERE pm.status = 'aktif' [AND m.nama ILIKE :namaMahasiswa];

  -- 3. Total Surat Belum Diunggah
  SELECT COUNT(1) FROM periode_magang pm JOIN mahasiswa m ON pm.mahasiswa_id = m.id LEFT JOIN surat_keterangan_magang sk ON pm.id = sk.periode_magang_id WHERE pm.status = 'aktif' AND sk.id IS NULL [AND m.nama ILIKE :namaMahasiswa];
  ```
- **Response Payload (`SuratKeteranganStatResponse` - HTTP 200 OK):**
  ```json
  {
    "totalSuratDiunggah": 159,
    "totalSuratBelumDiunggah": 18,
    "totalJumlahSurat": 177
  }
  ```
