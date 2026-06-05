# Absensi Mahasiswa (Attendance) API Documentation

This module manages student daily attendance logs, mentor approval workflows, sick/leave attachments, and rekap exports.

## 🚀 Endpoints Summary

| Method | Endpoint | Auth Required | Description |
|---|---|---|---|
| `GET` | `/api/absensi` | Yes | Retrieves list of attendance logs with optional filter parameters (Mentor). |
| `POST` | `/api/absensi/{id}/verifikasi` | Yes | Approves or rejects an attendance request (e.g. sick/leave approvals) (Mentor). |
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
  - `status` (String, optional): Filter by day status (e.g. `Hadir` | `Izin` | `Sakit` | `Alpha`)
  - `namaMahasiswa` (String, optional): Filter by student name substring
- **Response Payload (`List<AbsensiResponse>` - HTTP 200 OK):**
```json
[
  {
    "id": "e0a6d1b2-132d-4bfb-bdf0-4b95d3cbe7b4",
    "periodeMagangId": "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d",
    "mahasiswaId": "f81d4fae-7dec-11d0-a765-00a0c91e6bf6",
    "nim": "2201012001",
    "namaMahasiswa": "Budi Santoso",
    "tanggal": "2026-05-29", // yyyy-MM-dd
    "waktuMasuk": "2026-05-29T08:00:00+07:00", // ISO-8601
    "waktuKeluar": "2026-05-29T17:00:00+07:00",
    "status": "Hadir",
    "attachmentUrl": "https://storage.internflow.com/absensi/doc.pdf",
    "statusVerifikasi": "Disetujui" // e.g. "Draft" | "Diproses" | "Disetujui" | "Ditolak"
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
  "status": "Hadir",
  "attachmentUrl": "https://storage.internflow.com/absensi/doc.pdf",
  "statusVerifikasi": "Disetujui" // Will be updated depending on action
}
```

---

### 3. Delete Attendance Log
Removes an attendance record.

- **URL:** `/api/absensi/{id}`
- **Method:** `DELETE`
- **Headers:** `Authorization: Bearer <token>`
- **Path Parameter:**
  - `id` (UUID): ID of the attendance log
- **Response:** `204 No Content`

---

### 4. Attendance Statistics
Get total attendance counts (Mentor).

- **URL:** `/api/absensi/statistik`
- **Method:** `GET`
- **Headers:** `Authorization: Bearer <token>`
- **Query Parameter (Optional):**
  - `namaMahasiswa` (String): Filter statistics by student name
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
  "url": "https://storage.internflow.com/absensi/doc.pdf"
}
```

---

### 6. Export Attendance Data
Download a generated CSV format of all student attendance records (Mentor).

- **URL:** `/api/absensi/ekspor`
- **Method:** `GET`
- **Headers:** `Authorization: Bearer <token>`
- **Query Parameters:**
  - `status` (String, optional): Filter records by day status
  - `namaMahasiswa` (String, optional): Filter records by student name
- **Response Structure:** File Stream, returns a `.csv` file. Content type is `text/csv;charset=UTF-8`.
```csv
ID,NIM,Nama Mahasiswa,Tanggal,Status,Status Verifikasi
e0a6d1b2...,2201012001,Budi Santoso,2026-05-29,Hadir,Disetujui
```

---

### 7. Submit Daily Attendance (Mahasiswa)
Student submits their daily attendance entry. Supports files like doctor notes or event invitations.

- **URL:** `/api/absensi/mahasiswa/submit`
- **Method:** `POST`
- **Headers:** `Authorization: Bearer <token>`, `Content-Type: multipart/form-data`
- **Query Parameters:**
  - `userId` (UUID, Required): User ID of the student
  - `status` (String, Required): Must be one of `hadir` | `izin` | `sakit` (case-insensitive)
  - `keterangan` (String, Optional): Mandatory explanation/notes for `izin` and `sakit` status
- **Request Part (Multipart Form-Data):**
  - `file` (MultipartFile, Optional): Support attachment file, must be PDF or Image format, maximum size 10MB.
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
- **Response Payload (`AbsensiMahasiswaStatResponse` - HTTP 200 OK):**
```json
{
  "totalHadir": 76,
  "totalIzin": 1,
  "totalSakit": 2,
  "totalAlfa": 0
}
```
