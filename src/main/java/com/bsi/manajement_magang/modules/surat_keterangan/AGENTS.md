# Surat Keterangan (Completion Letters) API Documentation

This module manages official completion letters (surat keterangan selesai magang) issued to students after their internship periods conclude.

## 🚀 Endpoints Summary

| Method | Endpoint | Auth Required | Description |
|---|---|---|---|
| `GET` | `/api/surat-keterangan` | Yes | Retrieves list of student completion letters with optional filters. |
| `POST` | `/api/surat-keterangan` | Yes | Uploads/registers an official completion letter for a student's internship period. |
| `GET` | `/api/surat-keterangan/statistik` | Yes | Obtains statistical counters of completion letter statuses. |

---

## 📋 Detailed Endpoints & Payloads

### 1. List Completion Letters
Get uploaded completion letters with optional filters.

- **URL:** `/api/surat-keterangan`
- **Method:** `GET`
- **Headers:** `Authorization: Bearer <token>`
- **Query Parameters:**
  - `status` (String, optional): Filter by letter status (e.g. `Sudah Diunggah` | `Belum Diunggah`)
  - `namaMahasiswa` (String, optional): Filter by student name substring
- **Response Payload (`List<SuratKeteranganResponse>` - HTTP 200 OK):**
```json
[
  {
    "id": "e81d4fae-7dec-11d0-a765-00a0c91e6bf6",
    "periodeMagangId": "5c1a8d9b-2e9c-4aa4-8f7b-23fcd10d9e81",
    "mahasiswaId": "f81d4fae-7dec-11d0-a765-00a0c91e6bf6",
    "nim": "2201012001",
    "namaMahasiswa": "Budi Santoso",
    "url": "https://storage.internflow.com/letters/budi-completion-letter.pdf",
    "statusSurat": "Sudah Diunggah", // "Sudah Diunggah" | "Belum Diunggah"
    "createdAt": "2026-05-31T14:05:00"
  }
]
```

---

### 2. Upload/Register Completion Letter
Mentors or admins register a completion letter file.

- **URL:** `/api/surat-keterangan`
- **Method:** `POST`
- **Headers:** `Authorization: Bearer <token>`
- **Request Payload (`SuratKeteranganRequest`):**
```json
{
  "periodeMagangId": "5c1a8d9b-2e9c-4aa4-8f7b-23fcd10d9e81", // Required
  "url": "https://storage.internflow.com/letters/budi-completion-letter.pdf" // Required
}
```
- **Response Payload (`SuratKeteranganResponse` - HTTP 200 OK):**
```json
{
  "id": "e81d4fae-7dec-11d0-a765-00a0c91e6bf6",
  "periodeMagangId": "5c1a8d9b-2e9c-4aa4-8f7b-23fcd10d9e81",
  "mahasiswaId": "f81d4fae-7dec-11d0-a765-00a0c91e6bf6",
  "nim": "2201012001",
  "namaMahasiswa": "Budi Santoso",
  "url": "https://storage.internflow.com/letters/budi-completion-letter.pdf",
  "statusSurat": "Sudah Diunggah",
  "createdAt": "2026-05-31T14:05:00"
}
```

---

### 3. Completion Letter Statistics
Retrieve cumulative letter statistics.

- **URL:** `/api/surat-keterangan/statistik`
- **Method:** `GET`
- **Headers:** `Authorization: Bearer <token>`
- **Query Parameter (Optional):**
  - `namaMahasiswa` (String): Filter statistics by student name
- **Response Payload (`SuratKeteranganStatResponse` - HTTP 200 OK):**
```json
{
  "totalSuratDiunggah": 159,
  "totalSuratBelumDiunggah": 18,
  "totalJumlahSurat": 177
}
```
