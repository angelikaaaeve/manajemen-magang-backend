# Kegiatan Mahasiswa (Logbook) API Documentation

This module manages students' daily/weekly activity logbooks, comments, and approvals by industry supervisors/mentors.

## 📂 Code Files
- Controller: [DataKegiatanController.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/data_kegiatan/DataKegiatanController.java)
- Service: [DataKegiatanService.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/data_kegiatan/DataKegiatanService.java)
- Repository: [DataKegiatanRepository.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/data_kegiatan/DataKegiatanRepository.java)
- DTO Schemas:
  - [ActivityResponse.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/data_kegiatan/schema/ActivityResponse.java)
  - [ActivityStatResponse.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/data_kegiatan/schema/ActivityStatResponse.java)

---

## 🚀 Endpoints Summary

| Method | Endpoint | Auth Required | Description |
|---|---|---|---|
| `GET` | `/api/kegiatan` | Yes | Retrieves list of student daily activity logbooks with optional filters. |
| `PUT` | `/api/kegiatan/{id}/status` | Yes | Updates/Approves/Rejects the review status of a logbook entry. |
| `DELETE` | `/api/kegiatan/{id}` | Yes | Deletes a specific logbook entry. |
| `GET` | `/api/kegiatan/{id}/file` | Yes | Obtains the attachment download URL linked with this logbook entry. |
| `GET` | `/api/kegiatan/statistik` | Yes | Retrieves count metrics of total, approved, and rejected logbooks. |

---

## 📋 Detailed Endpoints & Payloads

### 1. List Logbook Entries
Get student activity logs with optional filter parameters.

- **URL:** `/api/kegiatan`
- **Method:** `GET`
- **Headers:** `Authorization: Bearer <token>`
- **Query Parameters:**
  - `status` (String, optional): Filter by logbook status (must be `disetujui` \| `belum disetujui` \| `ditolak`).
  - `namaMahasiswa` (String, optional): Filter by student name substring.
- **SQL Query Executed:**
  ```sql
  SELECT dk.id, pm.mahasiswa_id, m.nama as nama_mahasiswa, dk.judul, dk.deskripsi, 
         dk.waktu, fk.url as file_url, dk.status 
  FROM data_kegiatan dk 
  JOIN periode_magang pm ON dk.periode_magang_id = pm.id 
  JOIN mahasiswa m ON pm.mahasiswa_id = m.id 
  LEFT JOIN ( 
      SELECT DISTINCT ON (data_kegiatan_id) data_kegiatan_id, url 
      FROM file_kegiatan 
      ORDER BY data_kegiatan_id, created_at DESC 
  ) fk ON dk.id = fk.data_kegiatan_id 
  WHERE 1=1 
  -- IF status is provided
  AND dk.status = :status 
  -- IF namaMahasiswa is provided
  AND m.nama ILIKE :namaMahasiswa 
  ORDER BY dk.waktu DESC, m.nama ASC
  ```
- **Response Payload (`List<ActivityResponse>` - HTTP 200 OK):**
  ```json
  [
    {
      "id": "c1f7a8b9-4d2c-491c-8b8c-572e90cfa820", // UUID
      "mahasiswaId": "f81d4fae-7dec-11d0-a765-00a0c91e6bf6", // UUID
      "namaMahasiswa": "Budi Santoso",
      "judul": "Implementasi Integration Testing & Next.js Layout Refactoring",
      "deskripsi": "Melakukan refactoring terhadap layout utama admin dashboard untuk meningkatkan performa routing Next.js.",
      "waktu": "2026-05-29T17:00:00+07:00", // ISO-8601 OffsetDateTime
      "fileUrl": "/uploads/logbook/budi_report.pdf", // Nullable
      "status": "disetujui" // "disetujui" | "belum disetujui" | "ditolak"
    }
  ]
  ```

---

### 2. Update Logbook Status
Mentor verifies, approves, or rejects a logbook entry.

- **URL:** `/api/kegiatan/{id}/status`
- **Method:** `PUT`
- **Headers:** `Authorization: Bearer <token>`
- **Path Parameter:**
  - `id` (UUID): ID of the logbook entry
- **Query Parameter (Required):**
  - `status` (String): New status value, must be `disetujui`, `belum disetujui`, or `ditolak`
- **SQL Query Executed:**
  ```sql
  UPDATE data_kegiatan SET status = :status, updated_at = NOW() WHERE id = :id
  ```
- **Response Payload (`ActivityResponse` - HTTP 200 OK):**
  ```json
  {
    "id": "c1f7a8b9-4d2c-491c-8b8c-572e90cfa820",
    "mahasiswaId": "f81d4fae-7dec-11d0-a765-00a0c91e6bf6",
    "namaMahasiswa": "Budi Santoso",
    "judul": "Implementasi Integration Testing & Next.js Layout Refactoring",
    "deskripsi": "Melakukan refactoring terhadap layout utama admin dashboard untuk meningkatkan performa routing Next.js.",
    "waktu": "2026-05-29T17:00:00+07:00",
    "fileUrl": "/uploads/logbook/budi_report.pdf",
    "status": "disetujui"
  }
  ```

---

### 3. Delete Logbook Entry
Removes a specific logbook record.

- **URL:** `/api/kegiatan/{id}`
- **Method:** `DELETE`
- **Headers:** `Authorization: Bearer <token>`
- **Path Parameter:**
  - `id` (UUID): ID of the activity logbook
- **SQL Query Executed:**
  ```sql
  DELETE FROM data_kegiatan WHERE id = :id
  ```
- **Response:** `204 No Content`

---

### 4. Get Logbook File Attachment
Get download link for documents/reports uploaded by students as evidence of their weekly logbook tasks.

- **URL:** `/api/kegiatan/{id}/file`
- **Method:** `GET`
- **Headers:** `Authorization: Bearer <token>`
- **Path Parameter:**
  - `id` (UUID): ID of the activity logbook
- **Response Payload (HTTP 200 OK):**
  ```json
  {
    "url": "/uploads/logbook/budi_report.pdf"
  }
  ```

---

### 5. Logbook Activity Statistics
Get totals based on reviews, approvals, and flags.

- **URL:** `/api/kegiatan/statistik`
- **Method:** `GET`
- **Headers:** `Authorization: Bearer <token>`
- **Query Parameters (Optional):**
  - `status` (String): Filter statistics by status
  - `namaMahasiswa` (String): Filter statistics by student name
- **SQL Queries Executed:**
  ```sql
  -- Total count
  SELECT COUNT(1) FROM data_kegiatan dk 
  JOIN periode_magang pm ON dk.periode_magang_id = pm.id 
  JOIN mahasiswa m ON pm.mahasiswa_id = m.id 
  WHERE 1=1 [AND dk.status = :status] [AND m.nama ILIKE :namaMahasiswa];

  -- Disetujui count
  SELECT COUNT(1) FROM data_kegiatan dk 
  JOIN periode_magang pm ON dk.periode_magang_id = pm.id 
  JOIN mahasiswa m ON pm.mahasiswa_id = m.id 
  WHERE dk.status = 'disetujui' [AND m.nama ILIKE :namaMahasiswa];

  -- Ditolak count
  SELECT COUNT(1) FROM data_kegiatan dk 
  JOIN periode_magang pm ON dk.periode_magang_id = pm.id 
  JOIN mahasiswa m ON pm.mahasiswa_id = m.id 
  WHERE dk.status = 'ditolak' [AND m.nama ILIKE :namaMahasiswa];
  ```
- **Response Payload (`ActivityStatResponse` - HTTP 200 OK):**
  ```json
  {
    "totalKegiatan": 12,
    "disetujui": 10,
    "ditolak": 1
  }
  ```
