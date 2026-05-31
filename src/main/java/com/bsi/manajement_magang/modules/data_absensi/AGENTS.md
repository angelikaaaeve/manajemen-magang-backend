# Absensi Mahasiswa (Attendance) API Documentation

This module manages student daily attendance logs, mentor approval workflows, sick/leave attachments, and rekap exports.

## 🚀 Endpoints Summary

| Method | Endpoint | Auth Required | Description |
|---|---|---|---|
| `GET` | `/api/absensi` | Yes | Retrieves list of attendance logs with optional filter parameters. |
| `POST` | `/api/absensi/{id}/verifikasi` | Yes | Approves or rejects an attendance request (e.g. sick/leave approvals). |
| `DELETE` | `/api/absensi/{id}` | Yes | Deletes an attendance record. |
| `GET` | `/api/absensi/statistik` | Yes | Retrieves statistical counters of attendance. |
| `GET` | `/api/absensi/{id}/surat-keterangan` | Yes | Obtains the attachment file url (like medical certificate/leave request). |
| `GET` | `/api/absensi/ekspor` | Yes | Exports a raw CSV formatted logbook suitable for MS Excel. |

---

## 📋 Detailed Endpoints & Payloads

### 1. List Attendance Logs
Get student attendance entries with optional filter params.

- **URL:** `/api/absensi`
- **Method:** `GET`
- **Headers:** `Authorization: Bearer <token>`
- **Query Parameters:**
  - `status` (String, optional): Filter by day status (e.g. `Hadir` | `Izin` | `Sakit`)
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
Get total attendance counts.

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
Download a generated CSV format of all student attendance records.

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
