# Absensi Mahasiswa (Attendance) API Documentation

This module manages student daily attendance logs, mentor approval workflows, sick/leave attachments, and rekap exports.

## 📂 Code Files
- Controller: [DataAbsensiController.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/data_absensi/DataAbsensiController.java)
- Service: [DataAbsensiService.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/data_absensi/DataAbsensiService.java)
- Repository: [DataAbsensiRepository.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/data_absensi/DataAbsensiRepository.java)
- DTO Schemas:
  - [AbsensiResponse.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/data_absensi/schema/AbsensiResponse.java)
  - [AbsensiStatResponse.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/data_absensi/schema/AbsensiStatResponse.java)
  - [AbsensiMahasiswaStatResponse.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/data_absensi/schema/AbsensiMahasiswaStatResponse.java)

---

## 🚀 Endpoints Summary

| Method | Endpoint | Auth Required | Description |
|---|---|---|---|
| `GET` | `/api/absensi` | Yes | Retrieves list of attendance logs with optional filter parameters (Mentor). |
| `POST` | `/api/absensi/{id}/verifikasi` | Yes | Approves (`action=setujui`) or rejects (`action=tolak`) an attendance request (Mentor). |
| `DELETE` | `/api/absensi/{id}` | Yes | Deletes an attendance record (Mentor). |
| `GET` | `/api/absensi/statistik` | Yes | Retrieves statistical counters of attendance (Mentor). |
| `GET` | `/api/absensi/{id}/surat-keterangan` | Yes | Obtains the attachment file url (like medical certificate/leave request) (Mentor). |
| `GET` | `/api/absensi/ekspor` | Yes | Exports a raw CSV formatted logbook suitable for MS Excel (Mentor). |
| `POST` | `/api/absensi/mahasiswa/submit` | Yes | Submits student's daily attendance entry with optional file attachment (Mahasiswa). |
| `GET` | `/api/absensi/mahasiswa/riwayat` | Yes | Retrieves student's personal attendance history logs for the last 30 days (Mahasiswa). |
| `GET` | `/api/absensi/mahasiswa/statistik` | Yes | Retrieves private attendance statistics counters (Hadir, Izin, Sakit, Alfa) (Mahasiswa). |

---

## 📋 Detailed Endpoints & Payloads

### 1. List Attendance Logs
Get student attendance entries with optional filter params (Mentor).

- **URL:** `/api/absensi`
- **Method:** `GET`
- **Headers:** `Authorization: Bearer <token>`
- **Query Parameters:**
  - `status` (String, optional): Filter by day status (options: `hadir` | `izin` | `sakit` | `semua`).
  - `namaMahasiswa` (String, optional): Filter by student name substring (ILike query).
- **SQL Query Executed:**
  ```sql
  SELECT a.id, a.periode_magang_id, pm.mahasiswa_id, m.nim, m.nama as nama_mahasiswa, 
         a.tanggal, a.waktu_masuk, a.waktu_keluar, a.status, a.attachment_url, a.status_verifikasi 
  FROM absensi a 
  JOIN periode_magang pm ON a.periode_magang_id = pm.id 
  JOIN mahasiswa m ON pm.mahasiswa_id = m.id 
  WHERE 1=1 
  -- IF status is provided and not 'semua'
  AND a.status = :status 
  -- IF namaMahasiswa is provided
  AND m.nama ILIKE :namaMahasiswa 
  ORDER BY a.tanggal DESC, m.nama ASC
  ```
- **Response Payload (`List<AbsensiResponse>` - HTTP 200 OK):**
  ```json
  [
    {
      "id": "e0a6d1b2-132d-4bfb-bdf0-4b95d3cbe7b4", // UUID
      "periodeMagangId": "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d", // UUID
      "mahasiswaId": "f81d4fae-7dec-11d0-a765-00a0c91e6bf6", // UUID
      "nim": "2201012001",
      "namaMahasiswa": "Budi Santoso",
      "tanggal": "2026-05-29", // yyyy-MM-dd
      "waktuMasuk": "2026-05-29T08:00:00+07:00", // ISO-8601 OffsetDateTime
      "waktuKeluar": "2026-05-29T17:00:00+07:00", // ISO-8601 OffsetDateTime, can be null
      "status": "hadir", // e.g. "hadir" | "izin" | "sakit"
      "attachmentUrl": "/uploads/absensi/f81d4fae-7dec-11d0-a765-00a0c91e6bf6_2026-05-29_uuid.pdf", // Can be null or "-"
      "statusVerifikasi": "DISETUJUI" // e.g. "PENDING" | "DISETUJUI" | "DITOLAK"
    }
  ]
  ```

---

### 2. Verify Attendance
Mentor approves or rejects a student's sick/leave attendance request.

- **URL:** `/api/absensi/{id}/verifikasi`
- **Method:** `POST`
- **Headers:** `Authorization: Bearer <token>`
- **Path Parameter:**
  - `id` (UUID): ID of the attendance log
- **Query Parameter (Required):**
  - `action` (String): Action to perform, must be `setujui` or `tolak`
- **SQL Query Executed:**
  ```sql
  UPDATE absensi SET status_verifikasi = :statusVerifikasi WHERE id = :id
  ```
  *(Updates status_verifikasi to `'DISETUJUI'` if action is `'setujui'`, or `'DITOLAK'` if action is `'tolak'`)*
- **Response Payload (`AbsensiResponse` - HTTP 200 OK):**
  ```json
  {
    "id": "e0a6d1b2-132d-4bfb-bdf0-4b95d3cbe7b4",
    "periodeMagangId": "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d",
    "mahasiswaId": "f81d4fae-7dec-11d0-a765-00a0c91e6bf6",
    "nim": "2201012001",
    "namaMahasiswa": "Budi Santoso",
    "tanggal": "2026-05-29",
    "waktuMasuk": "2026-05-29T08:00:00+07:00",
    "waktuKeluar": "2026-05-29T17:00:00+07:00",
    "status": "hadir",
    "attachmentUrl": "/uploads/absensi/f81d4fae-7dec-11d0-a765-00a0c91e6bf6_2026-05-29_uuid.pdf",
    "statusVerifikasi": "DISETUJUI"
  }
  ```

---

### 3. Delete Attendance Log
Removes an attendance record from the database.

- **URL:** `/api/absensi/{id}`
- **Method:** `DELETE`
- **Headers:** `Authorization: Bearer <token>`
- **Path Parameter:**
  - `id` (UUID): ID of the attendance log
- **SQL Query Executed:**
  ```sql
  DELETE FROM absensi WHERE id = :id
  ```
- **Response:** `204 No Content`

---

### 4. Attendance Statistics
Get total attendance counts (Mentor).

- **URL:** `/api/absensi/statistik`
- **Method:** `GET`
- **Headers:** `Authorization: Bearer <token>`
- **Query Parameter (Optional):**
  - `namaMahasiswa` (String): Filter statistics by student name (ILike query).
- **SQL Query Executed:**
  ```sql
  -- Hadir count
  SELECT COUNT(1) FROM absensi a 
  JOIN periode_magang pm ON a.periode_magang_id = pm.id 
  JOIN mahasiswa m ON pm.mahasiswa_id = m.id 
  WHERE a.status = 'hadir' [AND m.nama ILIKE :namaMahasiswa];

  -- Izin/Sakit count
  SELECT COUNT(1) FROM absensi a 
  JOIN periode_magang pm ON a.periode_magang_id = pm.id 
  JOIN mahasiswa m ON pm.mahasiswa_id = m.id 
  WHERE a.status IN ('izin', 'sakit') [AND m.nama ILIKE :namaMahasiswa];
  ```
- **Response Payload (`AbsensiStatResponse` - HTTP 200 OK):**
  ```json
  {
    "totalHadir": 76,
    "totalIzinSakit": 3
  }
  ```

---

### 5. Get Attachment (Medical Cert/Leave Document)
Get the downloadable URL for sick leave proof.

- **URL:** `/api/absensi/{id}/surat-keterangan`
- **Method:** `GET`
- **Headers:** `Authorization: Bearer <token>`
- **Path Parameter:**
  - `id` (UUID): ID of the attendance log
- **Response Payload (HTTP 200 OK):**
  ```json
  {
    "url": "/uploads/absensi/f81d4fae-7dec-11d0-a765-00a0c91e6bf6_2026-05-29_uuid.pdf"
  }
  ```

---

### 6. Export Attendance Data
Download a generated CSV format of all student attendance records (Mentor).

- **URL:** `/api/absensi/ekspor`
- **Method:** `GET`
- **Headers:** `Authorization: Bearer <token>`
- **Query Parameters:**
  - `status` (String, optional): Filter records by status (`hadir` | `izin` | `sakit` | `semua`)
  - `namaMahasiswa` (String, optional): Filter records by student name
- **Response Structure:** File Stream, returns a `.csv` file. Content type is `text/csv;charset=UTF-8` and includes BOM `\ufeff`.
- **CSV Format (Semicolon `;` separated):**
  ```csv
  No;Tanggal;NIM;Nama Mahasiswa;Jam Masuk;Jam Keluar;Status Presensi;Status Verifikasi;URL Lampiran
  1;2026-05-29;2201012001;Budi Santoso;08:00:00;17:00:00;HADIR;DISETUJUI;/uploads/absensi/doc.pdf
  ```

---

### 7. Submit Daily Attendance (Mahasiswa)
Student submits their daily attendance entry. Supports doctor notes or leave permits via file uploads.

- **URL:** `/api/absensi/mahasiswa/submit`
- **Method:** `POST`
- **Headers:** `Authorization: Bearer <token>`, `Content-Type: multipart/form-data`
- **Query Parameters (Required):**
  - `userId` (UUID): User ID of the student
  - `status` (String): Must be one of `hadir` | `izin` | `sakit` (case-insensitive)
  - `keterangan` (String, Optional): Mandatory explanation/notes for `izin` and `sakit` status
- **Request Part (Multipart Form-Data):**
  - `file` (MultipartFile, Optional): Supports PDF or Image formats, maximum size 10MB.
- **Validation Rules:**
  - Requires an active internship period status (`'aktif'`).
  - Limits submissions to one entry per day per student.
- **SQL Queries Executed:**
  1. Finds active internship period:
     ```sql
     SELECT pm.id FROM periode_magang pm 
     JOIN mahasiswa m ON pm.mahasiswa_id = m.id 
     WHERE m.user_id = :userId AND pm.status = 'aktif' 
     ORDER BY pm.created_at DESC LIMIT 1
     ```
  2. Checks for duplicates:
     ```sql
     SELECT COUNT(1) FROM absensi 
     WHERE periode_magang_id = :periodeMagangId AND tanggal = :tanggal
     ```
  3. Inserts daily log entry:
     ```sql
     INSERT INTO absensi (id, periode_magang_id, tanggal, waktu_masuk, status, attachment_url, status_verifikasi) 
     VALUES (:id, :periodeMagangId, :tanggal, NOW(), :status, :attachmentUrl, 'PENDING')
     ```
- **Response Payload (`AbsensiResponse` - HTTP 201 Created):**
  ```json
  {
    "id": "e0a6d1b2-132d-4bfb-bdf0-4b95d3cbe7b4",
    "periodeMagangId": "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d",
    "mahasiswaId": "f81d4fae-7dec-11d0-a765-00a0c91e6bf6",
    "nim": "2201012001",
    "namaMahasiswa": "Budi Santoso",
    "tanggal": "2026-06-05",
    "waktuMasuk": "2026-06-05T08:15:00+07:00",
    "waktuKeluar": null,
    "status": "hadir",
    "attachmentUrl": null,
    "statusVerifikasi": "PENDING"
  }
  ```

---

### 8. Get Last 30 Days Attendance History (Mahasiswa)
Retrieves the logged-in student's daily attendance records for the last 30 days.

- **URL:** `/api/absensi/mahasiswa/riwayat`
- **Method:** `GET`
- **Headers:** `Authorization: Bearer <token>`
- **Query Parameter:**
  - `userId` (UUID, Required): Student's user ID.
- **SQL Query Executed:**
  ```sql
  SELECT a.id, a.periode_magang_id, pm.mahasiswa_id, m.nim, m.nama as nama_mahasiswa, 
         a.tanggal, a.waktu_masuk, a.waktu_keluar, a.status, a.attachment_url, a.status_verifikasi 
  FROM absensi a 
  JOIN periode_magang pm ON a.periode_magang_id = pm.id 
  JOIN mahasiswa m ON pm.mahasiswa_id = m.id 
  WHERE m.user_id = :userId 
  ORDER BY a.tanggal DESC 
  LIMIT 30
  ```
- **Response Payload (`List<AbsensiResponse>` - HTTP 200 OK):**
  ```json
  [
    {
      "id": "e0a6d1b2-132d-4bfb-bdf0-4b95d3cbe7b4",
      "periodeMagangId": "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d",
      "mahasiswaId": "f81d4fae-7dec-11d0-a765-00a0c91e6bf6",
      "nim": "2201012001",
      "namaMahasiswa": "Budi Santoso",
      "tanggal": "2026-06-05",
      "waktuMasuk": "2026-06-05T08:15:00+07:00",
      "waktuKeluar": "2026-06-05T17:00:00+07:00",
      "status": "hadir",
      "attachmentUrl": null,
      "statusVerifikasi": "DISETUJUI"
    }
  ]
  ```

---

### 9. Get Private Attendance Statistics (Mahasiswa)
Retrieves personal statistics counters (Hadir, Izin, Sakit, Alfa) for the student.

- **URL:** `/api/absensi/mahasiswa/statistik`
- **Method:** `GET`
- **Headers:** `Authorization: Bearer <token>`
- **Query Parameter:**
  - `userId` (UUID, Required): Student's user ID.
- **SQL Queries Executed:**
  - **Count Hadir:**
    ```sql
    SELECT COUNT(1) FROM absensi a JOIN periode_magang pm ON a.periode_magang_id = pm.id JOIN mahasiswa m ON pm.mahasiswa_id = m.id WHERE m.user_id = :userId AND a.status = 'hadir'
    ```
  - **Count Izin:**
    ```sql
    SELECT COUNT(1) FROM absensi a JOIN periode_magang pm ON a.periode_magang_id = pm.id JOIN mahasiswa m ON pm.mahasiswa_id = m.id WHERE m.user_id = :userId AND a.status = 'izin'
    ```
  - **Count Sakit:**
    ```sql
    SELECT COUNT(1) FROM absensi a JOIN periode_magang pm ON a.periode_magang_id = pm.id JOIN mahasiswa m ON pm.mahasiswa_id = m.id WHERE m.user_id = :userId AND a.status = 'sakit'
    ```
  - **Calculate Alfa:**
    ```sql
    SELECT GREATEST(0, 
      (SELECT GREATEST(0, (LEAST(CURRENT_DATE, pm2.tanggal_berakhir) - pm2.tanggal_mulai + 1)) 
       FROM periode_magang pm2 JOIN mahasiswa m2 ON pm2.mahasiswa_id = m2.id 
       WHERE m2.user_id = :userId ORDER BY pm2.created_at DESC LIMIT 1) 
      - (SELECT COUNT(1) FROM absensi a JOIN periode_magang pm ON a.periode_magang_id = pm.id JOIN mahasiswa m ON pm.mahasiswa_id = m.id WHERE m.user_id = :userId)
    )
    ```
- **Response Payload (`AbsensiMahasiswaStatResponse` - HTTP 200 OK):**
  ```json
  {
    "totalHadir": 76,
    "totalIzin": 1,
    "totalSakit": 2,
    "totalAlfa": 0
  }
  ```
