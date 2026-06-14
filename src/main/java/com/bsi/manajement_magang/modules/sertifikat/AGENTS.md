# Sertifikat (Internship Certificates) API Documentation

This module tracks completion certificates issued to students upon finishing their industrial internship program.

## 📂 Code Files
- Controller: [SertifikatController.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/sertifikat/controller/SertifikatController.java)
- Service: [SertifikatService.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/sertifikat/service/SertifikatService.java) (impl: [SertifikatServiceImpl.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/sertifikat/service/impl/SertifikatServiceImpl.java))
- Repository: [SertifikatRepository.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/sertifikat/repository/SertifikatRepository.java)
- DTO Schemas:
  - [SertifikatRequest.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/sertifikat/schema/request/SertifikatRequest.java)
  - [SertifikatResponse.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/sertifikat/schema/response/SertifikatResponse.java)
  - [SertifikatStatResponse.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/sertifikat/schema/response/SertifikatStatResponse.java)

---

## 🚀 Endpoints Summary

| Method | Endpoint | Auth Required | Description |
|---|---|---|---|
| `GET` | `/api/sertifikat` | Yes | Retrieves list of student internship certificates with optional filters. |
| `POST` | `/api/sertifikat` | Yes | Uploads/registers a completion certificate linked to an internship period. |
| `GET` | `/api/sertifikat/statistik` | Yes | Obtains statistical counters of certificate statuses. |

---

## 📋 Detailed Endpoints & Payloads

### 1. List Student Certificates
Get uploaded certificates with optional filters (Mentor).

- **URL:** `/api/sertifikat`
- **Method:** `GET`
- **Headers:** `Authorization: Bearer <token>`
- **Query Parameters:**
  - `status` (String, optional): Filter by certificate status (options: `sudah diunggah` | `belum diunggah` | `semua status` - case insensitive)
  - `namaMahasiswa` (String, optional): Filter by student name substring (ILike query)
- **SQL Query Executed:**
  ```sql
  SELECT pm.id as periode_id, pm.mahasiswa_id, m.nim, m.nama as nama_mahasiswa, 
         s.id as sertifikat_id, s.url, s.created_at 
  FROM periode_magang pm 
  JOIN mahasiswa m ON pm.mahasiswa_id = m.id 
  LEFT JOIN sertifikat s ON pm.id = s.periode_magang_id 
  WHERE pm.status = 'aktif' 
  -- IF status is 'sudah diunggah'
  AND s.id IS NOT NULL 
  -- IF status is 'belum diunggah'
  AND s.id IS NULL 
  -- IF namaMahasiswa parameter is provided
  AND m.nama ILIKE :namaMahasiswa 
  ORDER BY m.nama ASC
  ```
- **Response Payload (`List<SertifikatResponse>` - HTTP 200 OK):**
  - **Type:** JSON Array
  - **Structure:**
    ```json
    [
      {
        "id": "b10a9c8d-3f2e-4aa4-8f2c-5b3cf1e9a2b8", // UUID, can be null
        "periodeMagangId": "5c1a8d9b-2e9c-4aa4-8f7b-23fcd10d9e81", // UUID
        "mahasiswaId": "f81d4fae-7dec-11d0-a765-00a0c91e6bf6", // UUID
        "nim": "2201012001",
        "namaMahasiswa": "Budi Santoso",
        "url": "/uploads/certificates/budi_completion_cert.pdf", // String, defaults to "-" if none
        "statusSertifikat": "Sudah Diunggah", // "Sudah Diunggah" | "belum diunggah"
        "createdAt": "2026-05-31T14:00:00" // LocalDateTime (ISO-8601 without offset), can be null
      }
    ]
    ```

---

### 2. Upload/Register Certificate
Mentors or admins register a certificate URL link.

- **URL:** `/api/sertifikat`
- **Method:** `POST`
- **Headers:** `Authorization: Bearer <token>`, `Content-Type: application/json`
- **Request Payload (`SertifikatRequest`):**
  - **Type:** JSON
  - **Structure:**
    ```json
    {
      "periodeMagangId": "5c1a8d9b-2e9c-4aa4-8f7b-23fcd10d9e81", // Required (UUID)
      "url": "/uploads/certificates/budi_completion_cert.pdf" // Required (String)
    }
    ```
- **SQL Queries Executed:**
  1. Validates period exists:
     ```sql
     SELECT COUNT(1) FROM periode_magang WHERE id = :periodId
     ```
  2. Checks if certificate entry already exists for the period:
     ```sql
     SELECT pm.id as periode_id, ... FROM periode_magang pm LEFT JOIN sertifikat s ON pm.id = s.periode_magang_id ... WHERE pm.id = :periodId
     ```
  3. If exists, update:
     ```sql
     UPDATE sertifikat SET url = :url WHERE id = :id
     ```
  4. If not exists, insert:
     ```sql
     INSERT INTO sertifikat (id, periode_magang_id, url, created_at) VALUES (:id, :periodeMagangId, :url, NOW())
     ```
- **Response Payload (`SertifikatResponse` - HTTP 200 OK):**
  ```json
  {
    "id": "b10a9c8d-3f2e-4aa4-8f2c-5b3cf1e9a2b8",
    "periodeMagangId": "5c1a8d9b-2e9c-4aa4-8f7b-23fcd10d9e81",
    "mahasiswaId": "f81d4fae-7dec-11d0-a765-00a0c91e6bf6",
    "nim": "2201012001",
    "namaMahasiswa": "Budi Santoso",
    "url": "/uploads/certificates/budi_completion_cert.pdf",
    "statusSertifikat": "Sudah Diunggah",
    "createdAt": "2026-05-31T14:00:00"
  }
  ```

---

### 3. Certificate Upload Statistics
Retrieve cumulative certificate statistics (Mentor).

- **URL:** `/api/sertifikat/statistik`
- **Method:** `GET`
- **Headers:** `Authorization: Bearer <token>`
- **Query Parameter (Optional):**
  - `namaMahasiswa` (String): Filter statistics by student name (ILike query)
- **SQL Queries Executed:**
  ```sql
  -- 1. Total Jumlah Sertifikat (Active Periods)
  SELECT COUNT(1) FROM periode_magang pm JOIN mahasiswa m ON pm.mahasiswa_id = m.id WHERE pm.status = 'aktif' [AND m.nama ILIKE :namaMahasiswa];

  -- 2. Total Sertifikat Diunggah
  SELECT COUNT(1) FROM periode_magang pm JOIN mahasiswa m ON pm.mahasiswa_id = m.id JOIN sertifikat s ON pm.id = s.periode_magang_id WHERE pm.status = 'aktif' [AND m.nama ILIKE :namaMahasiswa];

  -- 3. Total Sertifikat Belum Diunggah
  SELECT COUNT(1) FROM periode_magang pm JOIN mahasiswa m ON pm.mahasiswa_id = m.id LEFT JOIN sertifikat s ON pm.id = s.periode_magang_id WHERE pm.status = 'aktif' AND s.id IS NULL [AND m.nama ILIKE :namaMahasiswa];
  ```
- **Response Payload (`SertifikatStatResponse` - HTTP 200 OK):**
  ```json
  {
    "totalSertifikatDiunggah": 159,
    "totalSertifikatBelumDiunggah": 18,
    "totalJumlahSertifikat": 177
  }
  ```
