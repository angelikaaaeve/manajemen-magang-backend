# Sertifikat (Internship Certificates) API Documentation

This module tracks the completion certificates issued to students upon finishing their industrial internship program.

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
  - `status` (String, optional): Filter by certificate status (must be lowercase in backend logic: `sudah diunggah` | `belum diunggah` or `semua status`)
  - `namaMahasiswa` (String, optional): Filter by student name substring
- **Response Payload (`List<SertifikatResponse>` - HTTP 200 OK):**
```json
[
  {
    "id": "b10a9c8d-3f2e-4aa4-8f2c-5b3cf1e9a2b8", // UUID
    "periodeMagangId": "5c1a8d9b-2e9c-4aa4-8f7b-23fcd10d9e81", // UUID
    "mahasiswaId": "f81d4fae-7dec-11d0-a765-00a0c91e6bf6", // UUID
    "nim": "2201012001",
    "namaMahasiswa": "Budi Santoso",
    "url": "https://storage.internflow.com/certificates/budi-completion-cert.pdf",
    "statusSertifikat": "Sudah Diunggah", // "Sudah Diunggah" | "Belum Diunggah"
    "createdAt": "2026-05-31T14:00:00" // LocalDateTime
  }
]
```

---

### 2. Upload/Register Certificate
Mentors or admins register a certificate file.

- **URL:** `/api/sertifikat`
- **Method:** `POST`
- **Headers:** `Authorization: Bearer <token>`, `Content-Type: application/json`
- **Request Payload (`SertifikatRequest`):**
```json
{
  "periodeMagangId": "5c1a8d9b-2e9c-4aa4-8f7b-23fcd10d9e81", // Required (UUID)
  "url": "https://storage.internflow.com/certificates/budi-completion-cert.pdf" // Required
}
```
- **Response Payload (`SertifikatResponse` - HTTP 200 OK):**
```json
{
  "id": "b10a9c8d-3f2e-4aa4-8f2c-5b3cf1e9a2b8",
  "periodeMagangId": "5c1a8d9b-2e9c-4aa4-8f7b-23fcd10d9e81",
  "mahasiswaId": "f81d4fae-7dec-11d0-a765-00a0c91e6bf6",
  "nim": "2201012001",
  "namaMahasiswa": "Budi Santoso",
  "url": "https://storage.internflow.com/certificates/budi-completion-cert.pdf",
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
  - `namaMahasiswa` (String): Filter statistics by student name
- **Response Payload (`SertifikatStatResponse` - HTTP 200 OK):**
```json
{
  "totalSertifikatDiunggah": 159,
  "totalSertifikatBelumDiunggah": 18,
  "totalJumlahSertifikat": 177
}
```
