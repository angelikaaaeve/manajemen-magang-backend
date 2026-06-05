# Penilaian (Evaluation & Grading) API Documentation

This module manages the internship evaluation process, enabling mentors to submit scores across multiple performance criteria (kinerja, kedisiplinan, etc.) and generate final results.

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
  - `status` (String, optional): Filter by appraisal status (e.g. `Sudah Dinilai` | `Belum Dinilai`)
  - `namaMahasiswa` (String, optional): Filter by student name substring
- **Response Payload (`List<PenilaianResponse>` - HTTP 200 OK):**
```json
[
  {
    "id": "a90f7d8c-4a3b-410d-8f2c-5b3cf1e9a2b8", // UUID
    "periodeMagangId": "5c1a8d9b-2e9c-4aa4-8f7b-23fcd10d9e81", // UUID
    "mahasiswaId": "f81d4fae-7dec-11d0-a765-00a0c91e6bf6", // UUID
    "nim": "2201012001",
    "namaMahasiswa": "Budi Santoso",
    "mentorId": "3cb1ab0d-4ea3-4cfb-81d0-d3cdb2413e11", // UUID
    "namaMentor": "Dr. Ahmad Hidayat, M.T.",
    "kinerja": 88.0,
    "kedisiplinan": 90.0,
    "tanggungJawab": 85.0,
    "komunikasi": 85.0,
    "sikap": 92.0,
    "kerapihan": 80.0,
    "absensi": 96.0,
    "kerjasama": 88.0,
    "nilaiTotal": 88.0, // Calculated average score
    "catatan": "Performa pengerjaan fitur integration test Next.js sangat baik dan rapi.",
    "statusPenilaian": "Sudah Dinilai" // "Sudah Dinilai" | "Belum Dinilai"
  }
]
```

---

### 2. Create or Update Student Evaluation
Industry supervisor submits or modifies scores for a student bimbingan.

- **URL:** `/api/penilaian`
- **Method:** `POST`
- **Headers:** `Authorization: Bearer <token>`, `Content-Type: application/json`
- **Request Payload (`PenilaianRequest`):**
```json
{
  "periodeMagangId": "5c1a8d9b-2e9c-4aa4-8f7b-23fcd10d9e81", // Required (UUID)
  "mentorId": "3cb1ab0d-4ea3-4cfb-81d0-d3cdb2413e11", // Required (UUID)
  "kinerja": 88.0, // Double [0-100]
  "kedisiplinan": 90.0, // Double [0-100]
  "tanggungJawab": 85.0, // Double [0-100]
  "komunikasi": 85.0, // Double [0-100]
  "sikap": 92.0, // Double [0-100]
  "kerapihan": 80.0, // Double [0-100]
  "absensi": 96.0, // Double [0-100]
  "kerjasama": 88.0, // Double [0-100]
  "catatan": "Performa pengerjaan fitur integration test Next.js sangat baik dan rapi."
}
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
  "nilaiTotal": 88.0,
  "catatan": "Performa pengerjaan fitur integration test Next.js sangat baik dan rapi.",
  "statusPenilaian": "Sudah Dinilai"
}
```

---

### 3. Evaluation Status Statistics
Retrieve general evaluation metrics counts.

- **URL:** `/api/penilaian/statistik`
- **Method:** `GET`
- **Headers:** `Authorization: Bearer <token>`
- **Query Parameter (Optional):**
  - `namaMahasiswa` (String): Filter statistics by student name
- **Response Payload (`PenilaianStatResponse` - HTTP 200 OK):**
```json
{
  "totalPenilaian": 177,
  "totalSudahDinilai": 159,
  "totalBelumDinilai": 18
}
```
